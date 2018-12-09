package com.amann.mimir_downloader.data.processed;

public class Question {
  String id;
  String title;
  // HTML
  String description;

  public Question(String id, String title, String description) {
    super();
    this.id = id;
    this.title = title;
    this.description = description;
  }

  public String getId() {
    return id;
  }
  
  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }
}
