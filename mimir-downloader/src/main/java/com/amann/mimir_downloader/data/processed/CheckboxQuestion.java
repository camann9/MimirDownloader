package com.amann.mimir_downloader.data.processed;

import java.util.List;

public final class CheckboxQuestion extends Question {
  private List<Integer> correctAnswerIdexes;
  private List<String> answers;

  public CheckboxQuestion(String title, String description, List<Integer> correctAnswerIdexes, List<String> answers) {
    super(title, description);
    this.correctAnswerIdexes = correctAnswerIdexes;
    this.answers = answers;
  }

  public List<Integer> getCorrectAnswerIdexes() {
    return correctAnswerIdexes;
  }

  public List<String> getAnswers() {
    return answers;
  }
}
