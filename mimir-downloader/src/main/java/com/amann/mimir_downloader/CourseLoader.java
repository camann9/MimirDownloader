package com.amann.mimir_downloader;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amann.mimir_downloader.data.Config;
import com.amann.mimir_downloader.data.Course;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;

public final class CourseLoader {
  final static String COURSE_URL_FORMAT = "https://class.mimir.io/lms/courses/%s";
  final static Pattern COURSE_URL_PATTERN =
      Pattern.compile("^https://class.mimir.io/courses/(.*)$");
  final static HttpRequestFactory requestFactory =
      new NetHttpTransport().createRequestFactory();
  final static Gson GSON = new Gson();
  
  public static Course loadCourse(String id, Config config) throws IOException {
    String url = String.format(COURSE_URL_FORMAT, id);
    return GSON.fromJson(Login.executeAuthedRequest(url, config), Course.class);
  }
  
  public static String readCourseUrl() {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.println("Input course URL (go to course in browser and copy URL)");
      Matcher m = COURSE_URL_PATTERN.matcher(scanner.nextLine());
      if (m.matches()) {
        return m.group(1);
      } else {
        System.out.println("Incorrect URL. Course URLs start with 'https://class.mimir.io/lms/courses/'.");
      }
    }
  }
}
