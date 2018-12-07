package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;
import com.amann.mimir_downloader.data.Config;
import com.google.gson.JsonSyntaxException;

public class MimirDownloader {
  public static void main(String[] args) throws JsonSyntaxException, IOException {
    String home = System.getProperty("user.home");
    File downloaderRoot = new File(home, ".mimir_downloader");
    Util.createDir(downloaderRoot);
    Config config = Util.readConfig(downloaderRoot);
    Login.getOrUpdateSession(config);
    Util.writeConfig(downloaderRoot, config);
  }
}
