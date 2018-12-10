package com.amann.mimir_downloader.data.json;

import java.util.List;

//Metadata about assignment that is part of the assignment JSON file.
public class RawAssignmentMetadata {
  // IDs of questions in order
  List<String> questionOrder;
  String id;
  String name;
  // HTML
  String description;

  public List<String> getQuestionOrder() {
    return questionOrder;
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
