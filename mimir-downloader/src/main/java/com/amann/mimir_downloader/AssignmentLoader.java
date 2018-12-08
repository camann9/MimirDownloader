package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.amann.mimir_downloader.data.json.Config;
import com.amann.mimir_downloader.data.json.RawAssignment;
import com.amann.mimir_downloader.data.processed.Assignment;

public final class AssignmentLoader {
  final static String ASSIGNMENT_URL_FORMAT = "https://class.mimir.io/lms/assignments/%s";


  public static Assignment loadAssignment(String id, Config config) throws IOException {
    String url = String.format(ASSIGNMENT_URL_FORMAT, id);
    String jsonFile = Networking.executeAuthedRequest(url, config);
    RawAssignment raw = Util.GSON.fromJson(jsonFile, RawAssignment.class);
    return processAssignment(raw);
  }

  public static Assignment loadAssignmentFromFile(File fileName) throws IOException {
    String contents = new String(Files.readAllBytes(fileName.toPath()));
    RawAssignment raw = Util.GSON.fromJson(contents, RawAssignment.class);
    return processAssignment(raw);
  }

  private static Assignment processAssignment(RawAssignment fromJson) {
    return new Assignment();
  }
}
