package com.amann.mimir_downloader.data.processed;

import java.util.List;

public final class CodeReviewQuestion extends Question {
  List<CodeFile> starterCode;

  public CodeReviewQuestion(String id, String title, String description,
      List<CodeFile> starterCode) {
    super(id, title, description);
    this.starterCode = starterCode;
  }

  public List<CodeFile> getStarterCode() {
    return starterCode;
  }
}
