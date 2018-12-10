package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

public final class AssignmentWriter {
  public static void writeAssignment(Assignment assignment, File folder,
      boolean overwrite) throws Exception {
    File target = new File(folder,
        Util.assignmentFileName(assignment.getName()));
    if (target.exists() && !overwrite) {
      throw new IOException(String.format(
          "Output file '%s' already exists and overwriting is disabled",
          target.toString()));
    }

    Document doc = Document.createShell("fake_url");
    generateHtml(assignment, doc);
    Util.printToFile(doc, target);
  }

  private static void generateHtml(Assignment assignment, Document doc) {
    doc.title(assignment.getName());
    Util.addHeaderElements(doc);
    generateBody(assignment, doc.body());
  }

  private static void generateBody(Assignment assignment, Element body) {
    Element header = body.appendElement("h1").text(assignment.getName());
    header.appendElement("a").addClass("examModeToggle").text("[hide answers]")
        .attr("title", "Hide/unhide answers (exam mode)");

    body.appendChild(new Element("div").html(assignment.getDescription())
        .addClass("assignmentDescription"));

    for (Question q : assignment.getQuestions()) {
      body.appendChild(generateQuestion(q));
    }
  }

  private static Element generateQuestion(Question question) {
    Element container = new Element("div").addClass("question");
    container.appendChild(new Element("h2").text(question.getTitle()));
    container.appendChild(new Element("div").html(question.getDescription())
        .addClass("questionDescription"));
    if (question instanceof ShortAnswerQuestion) {
      container.appendChild(new Element("div").addClass("shortAnswerField"));
    } else if (question instanceof LongAnswerQuestion) {
      container.appendChild(new Element("div").addClass("longAnswerField"));
    } else if (question instanceof FileUploadQuestion) {
      container.appendChild(new Element("div").addClass("fileUploadField"));
    } else if (question instanceof CheckboxQuestion) {
      container
          .appendChild(generateCheckboxQuestion((CheckboxQuestion) question));
    } else if (question instanceof MultipleChoiceQuestion) {
      container.appendChild(
          generateMultipleChoiceQuestion((MultipleChoiceQuestion) question));
    } else if (question instanceof CodeReviewQuestion) {
      container.appendChild(
          generateCodeReviewQuestion((CodeReviewQuestion) question));
    } else if (question instanceof CodingQuestion) {
      container.appendChild(generateCodingQuestion((CodingQuestion) question));
    } else {
      throw new IllegalArgumentException(
          "Unknown type of question: " + question.getClass().getSimpleName());
    }
    return container;
  }

  private static Element generateCheckboxQuestion(CheckboxQuestion question) {
    Element checkboxesList = new Element("ul");
    checkboxesList.addClass("optionList");
    for (int i = 0; i < question.getAnswers().size(); i++) {
      String answer = question.getAnswers().get(i);
      boolean correct = question.getCorrectAnswerIdexes().contains(i);
      Element box = new Element("li").text(answer);
      if (correct) {
        box.addClass("correct");
      } else {
        box.addClass("incorrect");
      }
      checkboxesList.appendChild(box);
    }
    return checkboxesList;
  }

  private static Element generateMultipleChoiceQuestion(
      MultipleChoiceQuestion question) {
    Element choicesList = new Element("ul");
    choicesList.addClass("optionList");
    for (int i = 0; i < question.getAnswers().size(); i++) {
      String answer = question.getAnswers().get(i);
      boolean correct = (question.getCorrectChoiceIndex() == i);
      Element box = new Element("li").text(answer);
      if (correct) {
        box.addClass("correct");
      } else {
        box.addClass("incorrect");
      }
      choicesList.appendChild(box);
    }
    return choicesList;
  }

  private static Element generateCodeReviewQuestion(
      CodeReviewQuestion question) {
    Element reviewDiv = new Element("div");
    reviewDiv.appendElement("h3").text("Code to review");
    reviewDiv.appendChild(generateFileList(question.getStarterCode()));
    return reviewDiv;
  }

  private static Element generateCodingQuestion(CodingQuestion question) {
    Element reviewDiv = new Element("div").addClass("codingQuestion");
    reviewDiv.appendElement("h3").text("Starter code");
    reviewDiv.appendChild(
        generateFileList(question.getStarterCode()).addClass("starterCode"));
    reviewDiv.appendElement("h3").text("Correct code");
    reviewDiv.appendChild(
        generateFileList(question.getCorrectCode()).addClass("correctCode"));
    reviewDiv.appendElement("div").addClass("examModeContainer");
    Element testCaseHeader = reviewDiv.appendElement("h3").text("Test cases");
    testCaseHeader.appendElement("a").text("[+]").addClass("hideLink")
        .attr("title", "hide/unhide test cases");
    reviewDiv.appendChild(generateTestCases(question.getTestCases()));
    return reviewDiv;
  }

  private static Element generateTestCases(List<CodeTestCase> testCases) {
    // Test cases are hidden by default
    Element testCaseDiv = new Element("div").addClass("testCases")
        .addClass("hidden");
    for (CodeTestCase testCase : testCases) {
      if (testCase instanceof CodeQualityTestCase) {
        // Ignoring these for now
        continue;
      }

      String type;
      Element testCaseElement;
      if (testCase instanceof IoTestCase) {
        type = "I/O test case";
        testCaseElement = generateIoTestCase((IoTestCase) testCase);
      } else if (testCase instanceof UnitTestCase) {
        type = "Unit test case";
        testCaseElement = generateUnitTestCase((UnitTestCase) testCase);
      } else if (testCase instanceof CustomTestCase) {
        type = "Custom test case";
        testCaseElement = generateCustomTestCase((CustomTestCase) testCase);
      } else {
        throw new IllegalArgumentException("Unknown type of test case: "
            + testCase.getClass().getSimpleName());
      }

      testCaseDiv.appendElement("h4").text(type + ": " + testCase.getName());
      testCaseDiv.appendChild(new Element("div").html(testCase.getDescription())
          .addClass("testCaseDescription"));
      testCaseDiv.appendChild(testCaseElement);
    }
    return testCaseDiv;
  }

  private static Element generateIoTestCase(IoTestCase testCase) {
    Element div = new Element("div");
    div.appendElement("h5").text("Input");
    div.appendElement("pre").appendElement("code").text(testCase.getInput())
        .addClass("ioInput");
    div.appendElement("h5").text("Expected output");
    div.appendElement("pre").appendElement("code")
        .text(testCase.getExpectedOutput()).addClass("ioOutput");
    return div;
  }

  private static Element generateUnitTestCase(UnitTestCase testCase) {
    Element div = new Element("div");
    div.appendElement("pre").appendElement("code").text(testCase.getTestCode())
        .addClass("unitTestCode");
    return div;
  }

  private static Element generateCustomTestCase(CustomTestCase testCase) {
    Element div = new Element("div");
    div.appendElement("h5").text("Test bash script");
    div.appendElement("pre").appendElement("code")
        .text(testCase.getTestBashScript()).addClass("bashScript");
    return div;
  }

  private static Element generateFileList(List<CodeFile> fileList) {
    Element filesDiv = new Element("div").addClass("fileList");
    for (CodeFile file : fileList) {
      filesDiv.appendElement("h4").text(file.getFileName());
      filesDiv.appendElement("pre").appendElement("code")
          .text(file.getContent()).addClass("codeFileField");
    }
    return filesDiv;
  }
}
