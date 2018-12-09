package com.amann.mimir_downloader.data.processed;

import java.util.ArrayList;
import java.util.List;

public class Course {
  private String name;
  private List<Assignment> assignments = new ArrayList<>();
  
  public Course(String name) {
    this.name = name;
  }

  public List<Assignment> getAssignments() {
    return assignments;
  }

  public void addAssignment(Assignment assignment) {
    assignments.add(assignment);
  }
}
