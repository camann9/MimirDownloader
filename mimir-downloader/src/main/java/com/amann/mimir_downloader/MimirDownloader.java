package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.amann.mimir_downloader.data.Config;
import com.amann.mimir_downloader.data.Course;
import com.google.gson.JsonSyntaxException;

public class MimirDownloader {
  public static void main(String[] args) throws JsonSyntaxException, IOException, ParseException {
    Options options = new Options();
    options.addOption("t", false, "display current time");
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse( options, args);
    
    String home = System.getProperty("user.home");
    File downloaderRoot = new File(home, ".mimir_downloader");
    Util.createDir(downloaderRoot);
    Config config = Util.readConfig(downloaderRoot);
    Login.getOrUpdateSession(config);
    Util.writeConfig(downloaderRoot, config);
    
    Course c = CourseLoader.loadCourse(CourseLoader.readCourseUrl(), config);
    System.out.println("It seems like you downloaded this course before.");
  }
}
