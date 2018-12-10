package com.amann.mimir_downloader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Instant;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.amann.mimir_downloader.data.json.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Util {
  public final static Gson GSON = new GsonBuilder()
      .registerTypeAdapter(Instant.class, new DateTypeAdapter()).create();
  final static String SESSION_TOKEN_COOKIE = "user_session_token";
  final static String SESSION_ID_COOKIE = "user_session_id";

  public static void createDir(File dir) {
    if (!dir.exists()) {
      dir.mkdir();
    }
  }

  public static Config readConfig(File downloaderRoot) throws IOException {
    File configFile = new File(downloaderRoot, "config.json");
    Config config = new Config();
    if (configFile.exists()) {
      String contents = new String(Files.readAllBytes(configFile.toPath()));
      return GSON.fromJson(contents, Config.class);
    } else {
      // Create empty config file
      writeConfig(downloaderRoot, config);
      return config;
    }
  }

  public static void writeConfig(File downloaderRoot, Config config)
      throws IOException {
    File configFile = new File(downloaderRoot, "config.json");
    String contents = GSON.toJson(config);
    Files.write(configFile.toPath(), contents.getBytes());
  }

  public static final void printToFile(Document xml, File outputFile)
      throws IOException {
    try (Writer out = new FileWriter(outputFile)) {
      out.write(xml.outerHtml());
    }
  }

  public static String assignmentFileName(String name) {
    return name.replaceAll("[^a-zA-Z _0-9]", "").replaceAll(" ", "_") + ".html";
  }
  
  public static void addHeaderElements(Document doc) {
    Element styleLink = doc.head().appendElement("link");
    styleLink.attr("rel", "stylesheet");
    styleLink.attr("type", "text/css");
    styleLink.attr("href", "style.css");
    Element jqueryLink = doc.head().appendElement("script");
    jqueryLink.attr("type", "text/javascript");
    jqueryLink.attr("src", "https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js");
    Element exporterScriptLink = doc.head().appendElement("script");
    exporterScriptLink.attr("type", "text/javascript");
    exporterScriptLink.attr("src", "code.js");
  }
}
