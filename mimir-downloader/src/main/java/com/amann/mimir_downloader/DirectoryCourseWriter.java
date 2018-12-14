package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.amann.mimir_downloader.data.processed.Assignment;
import com.amann.mimir_downloader.data.processed.Course;

public final class DirectoryCourseWriter {
  public static void writeCourse(Course course, File folder,
      boolean overwriteFiles) throws Exception {
    // Then write actual assignments in directory form
    for (Assignment assignment : course.getAssignments()) {
      AssignmentWriter.writeAssignmentCodeTree(assignment, folder, overwriteFiles);
    }
  }
}
