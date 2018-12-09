package com.amann.mimir_downloader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

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

  public static final void printToFile(Document xml, File outputFile)
      throws TransformerFactoryConfigurationError, IOException,
      TransformerException {
    Transformer tf = TransformerFactory.newInstance().newTransformer();
    tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tf.setOutputProperty(OutputKeys.INDENT, "yes");
    try (Writer out = new FileWriter(outputFile)) {
      tf.transform(new DOMSource(xml), new StreamResult(out));
    }
  }

}
