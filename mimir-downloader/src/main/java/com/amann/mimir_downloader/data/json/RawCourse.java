package com.amann.mimir_downloader.data.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class RawCourse {
  private String name;
  @SerializedName("allCoursework")
  private List<CourseAssignmentMetadata> assignments;

  public List<CourseAssignmentMetadata> getAssignments() {
    return assignments;
  }

  public void setAssignments(List<CourseAssignmentMetadata> assignments) {
    this.assignments = assignments;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
