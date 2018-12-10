package com.amann.mimir_downloader.data.processed;

import java.util.ArrayList;
import java.util.List;

public final class Assignment {
  private String id;
  private String name;
  private String description;
  private List<Question> questions = new ArrayList<>();

  public Assignment(String id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public List<Question> getQuestions() {
    return questions;
  }

  public void addQuestion(Question question) {
    questions.add(question);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
