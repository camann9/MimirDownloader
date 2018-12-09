package com.amann.mimir_downloader.data.processed;

import java.util.List;

public final class CodingQuestion extends Question {
  List<CodeFile> correctCode;
  List<CodeFile> starterCode;
  List<CodeTestCase> testCases;

  public CodingQuestion(String id, String title, String description,
      List<CodeFile> correctCode, List<CodeFile> starterCode,
      List<CodeTestCase> testCases) {
    super(id, title, description);
    this.correctCode = correctCode;
    this.starterCode = starterCode;
    this.testCases = testCases;
  }

  public List<CodeTestCase> getTestCases() {
    return testCases;
  }

  public List<CodeFile> getCorrectCode() {
    return correctCode;
  }

  public List<CodeFile> getStarterCode() {
    return starterCode;
  }
}
