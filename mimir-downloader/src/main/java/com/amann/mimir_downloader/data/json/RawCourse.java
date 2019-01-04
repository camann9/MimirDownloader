package com.amann.mimir_downloader.data.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class RawCourse {
  @SerializedName("course")
  private RawCourseMetadata metadata;
  @SerializedName("coursework")
  private List<CourseAssignmentMetadata> assignments;

  public List<CourseAssignmentMetadata> getAssignments() {
    return assignments;
  }

  public RawCourseMetadata getMetadata() {
    return metadata;
  }
}
