package com.amann.mimir_downloader;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import json.Config;
import json.Course;

public final class CourseLoader {
  final static String COURSE_URL_FORMAT = "https://class.mimir.io/lms/courses/%s";
  final static Pattern COURSE_URL_PATTERN = Pattern
      .compile("^https://class.mimir.io/courses/(.*)$");


  public static Course loadCourse(String id, Config config) throws IOException {
    String url = String.format(COURSE_URL_FORMAT, id);
    return Util.GSON.fromJson(Networking.executeAuthedRequest(url, config), Course.class);
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
