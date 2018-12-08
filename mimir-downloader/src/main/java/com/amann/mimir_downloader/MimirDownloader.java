package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.amann.mimir_downloader.data.json.AssignmentMetadata;
import com.amann.mimir_downloader.data.json.Config;
import com.amann.mimir_downloader.data.json.Course;
import com.amann.mimir_downloader.data.processed.Assignment;
import com.google.gson.JsonSyntaxException;

public class MimirDownloader {
  public static final String HELP_PREFIX = "mimir-downloader [OPTIONS] <course URL copied from browser> <target folder>";

  public static void main(String[] args)
      throws JsonSyntaxException, IOException, ParseException {
    Options options = new Options();
    options.addOption("u", "user", true, "mimir user name (email)");
    options.addOption("p", "password", true, "mimir password");
    options.addOption("h", "help", true, "print help");
    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd = parser.parse(options, args);
    List<String> otherArgs = cmd.getArgList();

    if (otherArgs.size() != 2 || cmd.hasOption('h')) {
      formatter.printHelp(HELP_PREFIX, options);
      return;
    }

    String courseUrl = otherArgs.get(0);
    File targetFolder = new File(otherArgs.get(1));
    
    if (!targetFolder.isDirectory() && !targetFolder.mkdirs()) {
      System.out.println("Could not create output directory");
      return;
    }

    String home = System.getProperty("user.home");
    File downloaderRoot = new File(home, ".mimir_downloader");
    Util.createDir(downloaderRoot);
    Config config = getAuthConfigFromArgs(cmd, downloaderRoot);
    if (config == null) {
      formatter.printHelp(HELP_PREFIX, options);
      return;
    }

    String courseId = CourseLoader.getCourseId(courseUrl);
    if (courseId == null) {
      System.out.println("Incorrect course URL. Course URLs start "
          + "with 'https://class.mimir.io/courses/'.");
      formatter.printHelp(HELP_PREFIX, options);
      return;
    }
    Course c = CourseLoader.loadCourse(courseId, config);
    for (AssignmentMetadata a : c.getAssignments()) {
      System.out.format("Loading assignment %s", a.getId());
      Assignment parsedAssignment = AssignmentLoader.loadAssignment(a.getId(), config);
    }
    System.out.println("It seems like you downloaded this course before.");
  }

  private static Config getAuthConfigFromArgs(CommandLine cmd, File downloaderRoot)
      throws IOException {
    Config config = Util.readConfig(downloaderRoot);

    if (cmd.hasOption('u') && !cmd.hasOption('p')
        || !cmd.hasOption('u') && cmd.hasOption('p')) {
      System.out.println(
          "Mimir user name and password need to be specified together");
      return null;
    }
    if (cmd.hasOption('u') && cmd.hasOption('p')) {
      // Sign user in and store credentials in config
      if (Networking.createSession(config, cmd.getOptionValue('u'),
          cmd.getOptionValue('p'))) {
        Util.writeConfig(downloaderRoot, config);
      } else {
        System.out.println("Invalid user name or password");
        return null;
      }
    }
    if (!Networking.verifySession(config)) {
      System.out.println("No valid Mimir session token in storage. "
          + "Must specify user name and password.");
      return null;
    }
    return config;
  }
}
