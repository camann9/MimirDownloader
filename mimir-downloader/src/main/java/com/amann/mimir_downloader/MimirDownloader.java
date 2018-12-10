package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.amann.mimir_downloader.data.json.Config;
import com.amann.mimir_downloader.data.processed.Assignment;
import com.amann.mimir_downloader.data.processed.Course;

public class MimirDownloader {
  public static final String HELP_PREFIX = "mimir-downloader [OPTIONS] <course URL copied from browser> <target (folder for multi-file)>";

  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption("u", "user", true, "mimir user name (email)");
    options.addOption("p", "password", true, "mimir password");
    options.addOption("o", "overwrite", false,
        "overwriting existing files in directory");
    options.addOption("f", "format", true,
        "ouput format: either multi-file (default) or single-file");
    options.addOption("h", "help", false, "print help");
    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd = parser.parse(options, args);
    
    List<String> otherArgs = cmd.getArgList();
    if (otherArgs.size() != 2 || cmd.hasOption('h')) {
      formatter.printHelp(HELP_PREFIX, options);
      return;
    }
    
    String format = cmd.hasOption('f') ? cmd.getOptionValue('f') : "multi-file";
    if (!(format.equals("multi-file") || format.equals("single-file"))) {
      System.out.println("Invalid output format.");
      formatter.printHelp(HELP_PREFIX, options);
      return;
    }
    boolean isMultiFile = format.equals("multi-file");

    String courseUrl = otherArgs.get(0);
    String target = otherArgs.get(1);
    boolean overwriteFiles = cmd.hasOption('o');

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
    
    Course course = CourseLoader.loadCourse(courseId, config);

    if (isMultiFile) {
      saveCourseMultiFile(course, target, overwriteFiles);
    } else {
      saveCourseSingleFile(course, target, overwriteFiles);
    }

//    Course course = CourseLoader
//        .loadCourseFromFile(new File("realCourse.json"));
//    CourseWriter.writeCourse(course, targetFolder, overwriteFiles);

//     Assignment parsedAssignment = AssignmentLoader
//         .loadAssignmentFromFile(new File("realAssignment2.json"));
//     AssignmentWriter.writeAssignment(parsedAssignment, targetFolder,
//         overwriteFiles);
  }

  private static void saveCourseMultiFile(Course course, String target,
      boolean overwriteFiles) throws IOException, Exception {
    File targetFolder = new File(target);
    if (!targetFolder.isDirectory() && !targetFolder.mkdirs()) {
      System.out.println("Could not create output directory");
      return;
    }
    Util.copyResources(targetFolder, overwriteFiles);
    MultiFileCourseWriter.writeCourse(course, targetFolder, overwriteFiles);
  }
  
  private static void saveCourseSingleFile(Course course, String target,
      boolean overwriteFiles) throws IOException, Exception {
    File targetFile = new File(target);
    Util.checkShouldOverwrite(targetFile, overwriteFiles);
    SingleFileCourseWriter.writeCourse(course, targetFile);
  }

  private static Config getAuthConfigFromArgs(CommandLine cmd,
      File downloaderRoot) throws IOException {
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
