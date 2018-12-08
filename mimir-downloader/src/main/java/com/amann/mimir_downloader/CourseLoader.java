package com.amann.mimir_downloader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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


  public static Course loadCourse(String id, Config config) throws IOException {
    String url = String.format(COURSE_URL_FORMAT, id);
    RawCourse raw = Util.GSON.fromJson(Networking.executeAuthedRequest(url, config), RawCourse.class);
    Map<String, Assignment> rawAssignments = new HashMap<>();
    for (CourseAssignmentMetadata a : raw.getAssignments()) {
      System.out.format("Loading assignment %s (%s)\n", a.getName(), a.getId());
      rawAssignments.put(a.getId(), AssignmentLoader.loadAssignment(a.getId(), config));
    }
    return new Course();
  }

  public static String getCourseId(String courseUrl) {
    Matcher m = COURSE_URL_PATTERN.matcher(courseUrl);
    if (m.matches()) {
      return m.group(1);
    } else {
      return null;
    }
  }
}
