package com.odde.doughnut.controllers;

import com.odde.doughnut.controllers.json.QuestionSuggestionCreationParams;
import com.odde.doughnut.controllers.json.QuizQuestion;
import com.odde.doughnut.controllers.json.QuizQuestionContestResult;
import com.odde.doughnut.entities.*;
import com.odde.doughnut.entities.quizQuestions.QuizQuestionAIQuestion;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.factoryServices.quizFacotries.factories.AiQuestionFactory;
import com.odde.doughnut.models.AnswerModel;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.services.AiAdvisorService;
import com.odde.doughnut.services.GlobalSettingsService;
import com.odde.doughnut.services.ai.AiQuestionGenerator;
import com.odde.doughnut.services.openAiApis.OpenAiApiHandler;
import com.odde.doughnut.testability.TestabilitySettings;
import com.theokanning.openai.client.OpenAiApi;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/quiz-questions")
class RestQuizQuestionController {
  private final AiAdvisorService aiAdvisorService;
  private final ModelFactoryService modelFactoryService;

  private final UserModel currentUser;

  @Resource(name = "testabilitySettings")
  private final TestabilitySettings testabilitySettings;

  private final GlobalSettingsService globalSettingsService;
  private final OpenAiApiHandler openAiHandler;

  public RestQuizQuestionController(
      @Qualifier("testableOpenAiApi") OpenAiApi openAiApi,
      ModelFactoryService modelFactoryService,
      UserModel currentUser,
      TestabilitySettings testabilitySettings) {
    this.aiAdvisorService = new AiAdvisorService(openAiApi);
    this.openAiHandler = new OpenAiApiHandler(openAiApi);
    this.modelFactoryService = modelFactoryService;
    this.currentUser = currentUser;
    this.testabilitySettings = testabilitySettings;
    this.globalSettingsService = new GlobalSettingsService(modelFactoryService);
  }

  @PostMapping("/generate-question")
  @Transactional
  public QuizQuestion generateQuestion(@RequestParam(value = "note") Note note) {
    currentUser.assertLoggedIn();
    return generateAIQuestion(note);
  }

  @PostMapping("/{quizQuestion}/contest")
  @Transactional
  public QuizQuestionContestResult contest(
      @PathVariable("quizQuestion") QuizQuestionAIQuestion quizQuestionEntity) {
    currentUser.assertLoggedIn();
    return getAiQuestionGenerator(globalSettingsService.getGlobalSettingEvaluation().getValue())
        .getQuizQuestionContestResult(quizQuestionEntity);
  }

  @PostMapping("/{quizQuestion}/regenerate")
  @Transactional
  public QuizQuestion regenerate(
      @PathVariable("quizQuestion") QuizQuestionEntity quizQuestionEntity) {
    currentUser.assertLoggedIn();
    return generateAIQuestion(quizQuestionEntity.getNote());
  }

  private QuizQuestion generateAIQuestion(Note note) {
    String questionGenerationModelName =
        globalSettingsService.getGlobalSettingQuestionGeneration().getValue();
    AiQuestionFactory aiQuestionFactory =
        new AiQuestionFactory(note, getAiQuestionGenerator(questionGenerationModelName));
    QuizQuestionEntity quizQuestionEntity = aiQuestionFactory.create();
    if (quizQuestionEntity == null) {
      throw (new ResponseStatusException(HttpStatus.NOT_FOUND, "No question generated"));
    }
    modelFactoryService.save(quizQuestionEntity);
    return modelFactoryService.toQuizQuestion(quizQuestionEntity);
  }

  private AiQuestionGenerator getAiQuestionGenerator(String questionGenerationModelName) {
    return new AiQuestionGenerator(openAiHandler, questionGenerationModelName);
  }

  @PostMapping("/{quizQuestion}/answer")
  @Transactional
  public AnsweredQuestion answerQuiz(
      @PathVariable("quizQuestion") QuizQuestionEntity quizQuestionEntity,
      @Valid @RequestBody Answer answer) {
    currentUser.assertLoggedIn();
    answer.setQuestion(quizQuestionEntity);
    AnswerModel answerModel = modelFactoryService.toAnswerModel(answer);
    answerModel.makeAnswerToQuestion(
        testabilitySettings.getCurrentUTCTimestamp(), currentUser.getEntity());
    return answerModel.getAnswerViewedByUser(currentUser.getEntity());
  }

  @PostMapping("/{quizQuestion}/suggest-fine-tuning")
  @Transactional
  public SuggestedQuestionForFineTuning suggestQuestionForFineTuning(
      @PathVariable("quizQuestion") QuizQuestionAIQuestion quizQuestionEntity,
      @Valid @RequestBody QuestionSuggestionCreationParams suggestion) {
    SuggestedQuestionForFineTuning sqft = new SuggestedQuestionForFineTuning();
    var suggestedQuestionForFineTuningService =
        modelFactoryService.toSuggestedQuestionForFineTuningService(sqft);
    return suggestedQuestionForFineTuningService.suggestQuestionForFineTuning(
        quizQuestionEntity,
        suggestion,
        currentUser.getEntity(),
        testabilitySettings.getCurrentUTCTimestamp());
  }
}
