package com.amann.mimir_downloader.data.processed;

import java.util.List;
import java.util.Set;

public final class CheckboxQuestion extends Question {
  private Set<Integer> correctAnswerIndexes;
  private List<String> answers;

  public CheckboxQuestion(String id, String title, String description,
      Set<Integer> correctAnswerIndexes, List<String> answers) {
    super(id, title, description);
    this.correctAnswerIndexes = correctAnswerIndexes;
    this.answers = answers;
  }

  public Set<Integer> getCorrectAnswerIdexes() {
    return correctAnswerIndexes;
  }

  public List<String> getAnswers() {
    return answers;
  }
}
