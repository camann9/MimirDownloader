package com.amann.mimir_downloader;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;

import json.Config;
import json.Course;

public final class CourseLoader {
  final static String COURSE_URL_FORMAT = "https://class.mimir.io/lms/courses/%s";
  final static Pattern COURSE_URL_PATTERN = Pattern
      .compile("^https://class.mimir.io/courses/(.*)$");
  final static HttpRequestFactory requestFactory = new NetHttpTransport()
      .createRequestFactory();
  final static Gson GSON = new Gson();

  public static Course loadCourse(String id, Config config) throws IOException {
    String url = String.format(COURSE_URL_FORMAT, id);
    return GSON.fromJson(Login.executeAuthedRequest(url, config), Course.class);
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
