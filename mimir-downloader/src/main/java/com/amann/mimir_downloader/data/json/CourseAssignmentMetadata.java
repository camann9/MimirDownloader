package com.amann.mimir_downloader.data.json;

// Metadata about assignment that is part of the course JSON file.
public final class CourseAssignmentMetadata {
  private String id;
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
