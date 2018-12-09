package com.amann.mimir_downloader.data.json;

import java.util.List;

public final class RawQuestion {
  String id;
  // Title
  String prompt;
  // One of long_answer, short_answer, multiple_choice, code_area
  String questionType;
  // HTML String
  String description;
  // Choices for multiple choice
  List<String> choices;
  int correctChoiceIndex;
  List<Integer> correctMultiIndexes;
  
  public String getId() {
    return id;
  }
  public String getPrompt() {
    return prompt;
  }
  public String getQuestionType() {
    return questionType;
  }
  public String getDescription() {
    return description;
  }
  public List<String> getChoices() {
    return choices;
  }
  public int getCorrectChoiceIndex() {
    return correctChoiceIndex;
  }
  public List<Integer> getCorrectMultiIndexes() {
    return correctMultiIndexes;
  }
}