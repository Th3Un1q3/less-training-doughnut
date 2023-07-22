package com.odde.doughnut.models;

import com.odde.doughnut.entities.*;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class AnswerModel {
  private final Answer answer;
  private final ModelFactoryService modelFactoryService;

  private Boolean cachedResult;

  public AnswerModel(Answer answer, ModelFactoryService modelFactoryService) {
    this.answer = answer;
    this.modelFactoryService = modelFactoryService;
  }

  public void updateReviewPoints(Timestamp currentUTCTimestamp) {
    boolean correct = isCorrect();
    relatedReviewPoints()
        .map(this.modelFactoryService::toReviewPointModel)
        .forEach(model -> model.updateAfterRepetition(currentUTCTimestamp, correct));
  }

  private Stream<ReviewPoint> relatedReviewPoints() {
    Stream<ReviewPoint> reviewPointStream =
        answer.getQuestion().getViceReviewPointIdList().stream()
            .flatMap(
                rPid -> this.modelFactoryService.reviewPointRepository.findById(rPid).stream());
    ReviewPoint reviewPoint = answer.getQuestion().getReviewPoint();
    if (reviewPoint != null) {
      return Stream.concat(reviewPointStream, Stream.of(reviewPoint));
    }
    return reviewPointStream;
  }

  public AnswerViewedByUser getAnswerViewedByUser() {
    AnswerViewedByUser answerResult = new AnswerViewedByUser();
    answerResult.answerId = answer.getId();
    answerResult.correct = isCorrect();
    answerResult.answerDisplay = getAnswerDisplay();
    return answerResult;
  }

  public AnswerResult getAnswerResult() {
    AnswerResult answerResult = new AnswerResult();
    answerResult.answerId = answer.getId();
    answerResult.correct = isCorrect();
    return answerResult;
  }

  public void save() {
    modelFactoryService.answerRepository.save(answer);
  }

  private String getAnswerDisplay() {
    Note answerNote = getAnswerNote();
    if (answerNote != null) {
      return answerNote.getTitle();
    }
    return answer.getSpellingAnswer();
  }

  private boolean isCorrect() {
    if (cachedResult != null) return cachedResult;
    QuizQuestionEntity question = answer.getQuestion();
    if (question.getCorrectAnswerIndex() != null) {
      cachedResult = Objects.equals(answer.getChoiceIndex(), question.getCorrectAnswerIndex());
    } else {
      cachedResult = question.buildPresenter().isAnswerCorrect(answer);
    }
    return cachedResult;
  }

  private Note getAnswerNote() {
    if (answer.getAnswerNoteId() != null) {
      return this.modelFactoryService
          .noteRepository
          .findById(answer.getAnswerNoteId())
          .orElse(null);
    }
    if (answer.getChoiceIndex() != null) {
      return getChoiceThingAt(answer.getQuestion(), answer.getChoiceIndex())
          .map(thing -> thing.getLink() != null ? thing.getLink().getSourceNote() : thing.getNote())
          .orElse(null);
    }
    return null;
  }

  private Optional<Thing> getChoiceThingAt(QuizQuestionEntity question, Integer choiceIndex) {
    Integer thingId = question.getChoiceThingIdAt(choiceIndex);

    return modelFactoryService.thingRepository.findById(thingId);
  }
}
