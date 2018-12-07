package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.amann.mimir_downloader.data.Config;
import com.google.gson.Gson;

public final class Util {
  final static Gson gson = new Gson();
  
  public static void createDir(File dir) {
    if (! dir.exists()){
      dir.mkdir();
    }
  }

  public static Config readConfig(File downloaderRoot) throws IOException {
    File configFile = new File(downloaderRoot, "config.json");
    Config config = new Config();
    if (configFile.exists()) {
      String contents = new String(Files.readAllBytes(configFile.toPath()));
      return gson.fromJson(contents, Config.class);
    } else {
      // Create empty config file
      writeConfig(downloaderRoot, config);
      return config;
    }
  }
  
  public static void writeConfig(File downloaderRoot, Config config) throws IOException {
    File configFile = new File(downloaderRoot, "config.json");
    String contents = gson.toJson(config);
    Files.write(configFile.toPath(), contents.getBytes());
  }
}
