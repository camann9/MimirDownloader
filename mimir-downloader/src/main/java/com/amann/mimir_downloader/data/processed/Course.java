package com.amann.mimir_downloader.data.processed;

import java.util.ArrayList;
import java.util.List;

public class Course {
  private List<Assignment> assignments = new ArrayList<>();

  List<Assignment> getAssignments() {
    return assignments;
  }

  void addAssignments(Assignment assignment) {
    assignments.add(assignment);
  }
}
