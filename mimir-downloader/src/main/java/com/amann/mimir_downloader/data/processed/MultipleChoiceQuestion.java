package com.amann.mimir_downloader.data.processed;

import java.util.List;

public final class MultipleChoiceQuestion extends Question {
  private int correctChoiceIndex;
  private List<String> answers;

  public MultipleChoiceQuestion(String id, String title, String description,
      int correctChoiceIndex, List<String> answers) {
    super(id, title, description);
    this.correctChoiceIndex = correctChoiceIndex;
    this.answers = answers;
  }

  public int getCorrectChoiceIndex() {
    return correctChoiceIndex;
  }

  public List<String> getAnswers() {
    return answers;
  }

}
