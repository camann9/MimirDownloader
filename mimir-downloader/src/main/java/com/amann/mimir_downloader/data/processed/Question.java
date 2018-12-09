package com.amann.mimir_downloader.data.processed;

public class Question {
  String title;
  // HTML
  String description;

  public Question(String title, String description) {
    super();
    this.title = title;
    this.description = description;
  }
}
