package com.odde.doughnut.controllers;

import com.odde.doughnut.controllers.json.FineTuningExampleForQuestionGeneration;
import com.odde.doughnut.controllers.json.QuestionSuggestionParams;
import com.odde.doughnut.entities.SuggestedQuestionForFineTuning;
import com.odde.doughnut.exceptions.UnexpectedNoAccessRightException;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.models.UserModel;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@RestController
@SessionScope
@RequestMapping("/api/fine-tuning")
class RestFineTuningDataController {
  private final ModelFactoryService modelFactoryService;
  private UserModel currentUser;

  public RestFineTuningDataController(
      ModelFactoryService modelFactoryService, UserModel currentUser) {
    this.modelFactoryService = modelFactoryService;
    this.currentUser = currentUser;
  }

  @PatchMapping("/{suggestedQuestion}/update-suggested-question-for-fine-tuning")
  @Transactional
  public SuggestedQuestionForFineTuning updateSuggestedQuestionForFineTuning(
      @PathVariable("suggestedQuestion") SuggestedQuestionForFineTuning suggestedQuestion,
      @RequestBody QuestionSuggestionParams suggestion)
      throws UnexpectedNoAccessRightException {
    currentUser.assertAdminAuthorization();
    return modelFactoryService
        .toSuggestedQuestionForFineTuningService(suggestedQuestion)
        .update(suggestion);
  }

  @GetMapping("/question-generation-examples")
  public List<FineTuningExampleForQuestionGeneration>
      getAllPositiveQuestionGenerationFineTuningExamples() throws UnexpectedNoAccessRightException {
    currentUser.assertAdminAuthorization();
    return getSuggestedQuestionForFineTunings().stream()
        .map(SuggestedQuestionForFineTuning::toFineTuningExample)
        .toList();
  }

  @GetMapping("/all-positive-suggested-questions-for-fine-tuning")
  public List<SuggestedQuestionForFineTuning> getAllSuggestedQuestions()
      throws UnexpectedNoAccessRightException {
    currentUser.assertAdminAuthorization();
    return getSuggestedQuestionForFineTunings();
  }

  private List<SuggestedQuestionForFineTuning> getSuggestedQuestionForFineTunings() {
    List<SuggestedQuestionForFineTuning> suggestedQuestionForFineTunings = new ArrayList<>();
    modelFactoryService
        .questionSuggestionForFineTuningRepository
        .findAll()
        .forEach(suggestedQuestionForFineTunings::add);
    return suggestedQuestionForFineTunings;
  }
}
