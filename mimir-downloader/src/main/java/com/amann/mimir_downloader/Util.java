package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.amann.mimir_downloader.data.json.Config;
import com.google.gson.Gson;

public final class Util {
  public final static Gson GSON = new Gson();
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
}
