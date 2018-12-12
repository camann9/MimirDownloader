package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amann.mimir_downloader.data.json.Config;
import com.amann.mimir_downloader.data.json.CourseAssignmentMetadata;
import com.amann.mimir_downloader.data.json.RawCourse;
import com.amann.mimir_downloader.data.processed.Assignment;
import com.amann.mimir_downloader.data.processed.Course;

public final class CourseLoader {
  final static String COURSE_URL_FORMAT = "https://class.mimir.io/lms/courses/%s";
  final static Pattern COURSE_URL_PATTERN = Pattern
      .compile("^https://class.mimir.io/courses/(.*)$");

  public static Course loadCourse(String id, Config config)
      throws IOException, ParseException {
    String url = String.format(COURSE_URL_FORMAT, id);
    RawCourse raw = Util.GSON.fromJson(
        Networking.executeAuthedRequest(url, config), RawCourse.class);
    Course course = new Course(raw.getMetadata().getName());

    raw.getAssignments().sort(new AssignmentDateComparator());
    for (CourseAssignmentMetadata a : raw.getAssignments()) {
      if (a.getWorkType().equals("assignment")) {
        System.out.format("Loading assignment %s (%s)\n", a.getName(), a.getId());
        course.addAssignment(AssignmentLoader.loadAssignment(a.getId(), config));
      } else {
        System.out.format("Unsupported type of coursework %s in '%s'\n", a.getWorkType(), a.getName());
      }
    }
    return course;
  }

  public static Course loadCourseFromFile(File fileName) throws IOException {
    String contents = new String(Files.readAllBytes(fileName.toPath()));
    RawCourse raw = Util.GSON.fromJson(contents, RawCourse.class);
    Course course = new Course(raw.getMetadata().getName());

    raw.getAssignments().sort(new AssignmentDateComparator());
    for (CourseAssignmentMetadata a : raw.getAssignments()) {
      course.addAssignment(new Assignment(a.getId(), a.getName(), ""));
    }
    return course;
  }

  public static String getCourseId(String courseUrl) {
    Matcher m = COURSE_URL_PATTERN.matcher(courseUrl);
    if (m.matches()) {
      return m.group(1);
    } else {
      return null;
    }
  }

  private static final class AssignmentDateComparator
      implements Comparator<CourseAssignmentMetadata> {
    @Override
    public int compare(CourseAssignmentMetadata o1,
        CourseAssignmentMetadata o2) {
      return o1.getOpenDate().compareTo(o2.getOpenDate());
    }
  }
}
