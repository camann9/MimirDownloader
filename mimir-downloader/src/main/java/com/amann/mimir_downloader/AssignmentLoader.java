package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.amann.mimir_downloader.data.json.CompilatorEntry;
import com.amann.mimir_downloader.data.json.CompilatorValue;
import com.amann.mimir_downloader.data.json.Config;
import com.amann.mimir_downloader.data.json.FileMapEntry;
import com.amann.mimir_downloader.data.json.RawAssignment;
import com.amann.mimir_downloader.data.json.RawAssignmentMetadata;
import com.amann.mimir_downloader.data.json.RawQuestion;
import com.amann.mimir_downloader.data.json.RawTestCase;
import com.amann.mimir_downloader.data.processed.Assignment;
import com.amann.mimir_downloader.data.processed.CheckboxQuestion;
import com.amann.mimir_downloader.data.processed.CodeFile;
import com.amann.mimir_downloader.data.processed.CodeQualityTestCase;
import com.amann.mimir_downloader.data.processed.CodeReviewQuestion;
import com.amann.mimir_downloader.data.processed.CodeTestCase;
import com.amann.mimir_downloader.data.processed.CodingQuestion;
import com.amann.mimir_downloader.data.processed.CustomTestCase;
import com.amann.mimir_downloader.data.processed.FileUploadQuestion;
import com.amann.mimir_downloader.data.processed.IoTestCase;
import com.amann.mimir_downloader.data.processed.LongAnswerQuestion;
import com.amann.mimir_downloader.data.processed.MultipleChoiceQuestion;
import com.amann.mimir_downloader.data.processed.Question;
import com.amann.mimir_downloader.data.processed.ShortAnswerQuestion;
import com.amann.mimir_downloader.data.processed.UnitTestCase;

public final class AssignmentLoader {
  final static String ASSIGNMENT_URL_FORMAT = "https://class.mimir.io/lms/assignments/%s/edit";

  public static Assignment loadAssignment(String id, Config config)
      throws IOException, ParseException {
    String url = String.format(ASSIGNMENT_URL_FORMAT, id);
    String jsonFile = Networking.executeAuthedRequest(url, config);
    RawAssignment raw = Util.GSON.fromJson(jsonFile, RawAssignment.class);
    return processAssignment(raw);
  }

  public static Assignment loadAssignmentFromFile(File fileName)
      throws IOException, ParseException {
    String contents = new String(Files.readAllBytes(fileName.toPath()));
    RawAssignment raw = Util.GSON.fromJson(contents, RawAssignment.class);
    return processAssignment(raw);
  }

  private static Assignment processAssignment(RawAssignment rawAssignment)
      throws ParseException {
    RawAssignmentMetadata rawMetadata = rawAssignment.getAssignment();
    Assignment assignment = new Assignment(rawMetadata.getName(),
        rawMetadata.getDescription());

    // We need to map questions by ID to later retrieve them by the IDs in the
    // question order
    HashMap<String, RawQuestion> questionsById = new HashMap<>();
    for (RawQuestion question : rawAssignment.getQuestions()) {
      questionsById.put(question.getId(), question);
    }

    // Make list of compilators into actual map (by question
    HashMap<String, CompilatorValue> compilatorMap = new HashMap<>();
    for (CompilatorEntry e : rawAssignment.getCompilatorMap().getEntries()) {
      compilatorMap.put(e.getKey(), e.getValue());
    }

    for (String questionId : rawMetadata.getQuestionOrder()) {
      RawQuestion rawQuestion = questionsById.get(questionId);
      if (rawQuestion == null) {
        throw new ParseException(
            String.format("Question with ID %s not found in assignment '%s'",
                questionId, rawMetadata.getName()));
      }
      assignment.addQuestion(processQuestion(rawQuestion, compilatorMap));
    }
    return assignment;
  }

  private static Question processQuestion(RawQuestion rawQuestion,
      Map<String, CompilatorValue> compilatorMap) throws ParseException {
    String id = rawQuestion.getId();
    String title = rawQuestion.getPrompt();
    String description = rawQuestion.getDescription();
    CompilatorValue code = compilatorMap.get(id);

    String questionType = rawQuestion.getQuestionType();
    switch (questionType) {
    case "long_answer":
      return new LongAnswerQuestion(id, title, description);
    case "short_answer":
      return new ShortAnswerQuestion(id, title, description);
    case "file_upload":
      return new FileUploadQuestion(id, title, description);
    case "multiple_choice":
      return new MultipleChoiceQuestion(id, title, description,
          rawQuestion.getCorrectChoiceIndex(), rawQuestion.getChoices());
    case "multi_select":
      return new CheckboxQuestion(id, title, description,
          new HashSet<Integer>(rawQuestion.getCorrectMultiIndexes()),
          rawQuestion.getChoices());
    case "code_area":
      if (code == null) {
        throw new ParseException(
            String.format("Missing code for question '%s'", title));
      }
      return new CodingQuestion(id, title, description, getCorrectCode(code),
          getStarterCode(code), getTestCases(code, title));
    case "code_review":
      if (code == null) {
        throw new ParseException(
            String.format("Missing code for question '%s'", title));
      }
      return new CodeReviewQuestion(id, title, description,
          getStarterCode(code));
    default:
      throw new ParseException(String.format(
          "Unknown question type %s in question '%s'", questionType, title));
    }
  }

  private static List<CodeFile> getStarterCode(CompilatorValue code) {
    return processFiles(
        code.getSkeletonWorkingState().getFileMap().getEntries());
  }

  private static List<CodeFile> getCorrectCode(CompilatorValue code) {
    return processFiles(
        code.getPerfectWorkingState().getFileMap().getEntries());
  }

  private static List<CodeFile> processFiles(List<FileMapEntry> entries) {
    ArrayList<CodeFile> files = new ArrayList<>();
    for (FileMapEntry entry : entries) {
      files.add(new CodeFile(entry.getKey(), entry.getValue().getContent()));
    }
    return files;
  }

  private static List<CodeTestCase> getTestCases(CompilatorValue code,
      String questionName) throws ParseException {
    ArrayList<CodeTestCase> testCases = new ArrayList<>();
    for (RawTestCase test : code.getTestCases()) {
      testCases.add(processTestCase(test, questionName));
    }
    testCases.sort(new TestCaseNameComparator());
    return testCases;
  }

  private static CodeTestCase processTestCase(RawTestCase test,
      String questionName) throws ParseException {
    String testCaseType = test.getTestType();
    switch (testCaseType) {
    case "unit":
      return new UnitTestCase(test.getName(), test.getDescription(),
          test.getInput());
    case "io":
      return new IoTestCase(test.getName(), test.getDescription(),
          test.getInput(), test.getExpectedOutput());
    case "lint":
      return new CodeQualityTestCase(test.getName(), test.getDescription());
    case "custom":
      return new CustomTestCase(test.getName(), test.getDescription(),
          test.getInput());
    default:
      throw new ParseException(
          String.format("Unknown test case type %s in question '%s'",
              testCaseType, questionName));
    }
  }

  private static final class TestCaseNameComparator
      implements Comparator<CodeTestCase> {
    @Override
    public int compare(CodeTestCase o1, CodeTestCase o2) {
      return o1.getName().compareTo(o2.getName());
    }
  }
}
