package com.odde.doughnut.models.quizFacotries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odde.doughnut.entities.QuizQuestionEntity;
import com.odde.doughnut.entities.json.AIGeneratedQuestion;

public class AiQuestionPresenter implements QuizQuestionPresenter {
  private final AIGeneratedQuestion aiQuestion;

  public AiQuestionPresenter(QuizQuestionEntity quizQuestion) {
    try {
      this.aiQuestion =
          new ObjectMapper()
              .readValue(quizQuestion.getRawJsonQuestion(), AIGeneratedQuestion.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String instruction() {
    return aiQuestion.stem;
  }

  @Override
  public String mainTopic() {
    return null;
  }

  @Override
  public boolean isAnswerCorrect(String spellingAnswer) {
    return aiQuestion.correctChoice.equals(spellingAnswer);
  }
}
