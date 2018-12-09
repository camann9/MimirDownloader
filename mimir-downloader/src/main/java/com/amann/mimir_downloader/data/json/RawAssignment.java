package com.amann.mimir_downloader.data.json;

import java.util.List;

public final class RawAssignment {
  RawAssignmentMetadata assignment;
  List<RawQuestion> questions;
  CompilatorMap compilatorMap;
  
  public RawAssignmentMetadata getAssignment() {
    return assignment;
  }
  public List<RawQuestion> getQuestions() {
    return questions;
  }
  public CompilatorMap getCompilatorMap() {
    return compilatorMap;
  }
}
