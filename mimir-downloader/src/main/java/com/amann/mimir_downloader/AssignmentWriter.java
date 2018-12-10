package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

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
    Element styleLink = doc.head().appendElement("link");
    styleLink.attr("rel", "stylesheet");
    styleLink.attr("type", "text/css");
    styleLink.attr("href", "style.css");
    generateBody(assignment, doc.body());
  }

  private static void generateBody(Assignment assignment, Element body) {
    body.appendChild(new Element("h1").text(assignment.getName()));

    body.appendChild(new Element("div").html(assignment.getDescription())
        .addClass("assignmentDescription"));

    for (Question q : assignment.getQuestions()) {
      body.appendChild(generateQuestion(q));
    }
  }

  private static Element generateQuestion(Question question) {
    Element container = new Element("div");
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
    Element checkboxesDiv = new Element("div");
    for (int i = 0; i < question.getAnswers().size(); i++) {
      String answer = question.getAnswers().get(i);
      boolean correct = question.getCorrectAnswerIdexes().contains(i);
      Element box = new Element("input").attr("type", "checkbox").text(answer);
      if (correct) {
        box.attr("checked", true);
      }
      checkboxesDiv.appendChild(box);
      checkboxesDiv.appendElement("br");
    }
    return checkboxesDiv;
  }

  private static Element generateMultipleChoiceQuestion(
      MultipleChoiceQuestion question) {
    Element choicesDiv = new Element("div");
    for (int i = 0; i < question.getAnswers().size(); i++) {
      String answer = question.getAnswers().get(i);
      boolean correct = (question.getCorrectChoiceIndex() == i);
      Element box = new Element("input").attr("type", "radio")
          .attr("name", question.getId()).text(answer);
      if (correct) {
        box.attr("checked", true);
      }
      choicesDiv.appendChild(box);
      choicesDiv.appendElement("br");
    }
    return choicesDiv;
  }

  private static Element generateCodeReviewQuestion(
      CodeReviewQuestion question) {
    Element reviewDiv = new Element("div");
    reviewDiv.appendElement("h3").text("Code to review");
    reviewDiv.appendChild(generateFileList(question.getStarterCode()));
    return reviewDiv;
  }

  private static Element generateCodingQuestion(CodingQuestion question) {
    Element reviewDiv = new Element("div");
    reviewDiv.appendElement("h3").text("Starter code");
    reviewDiv.appendChild(generateFileList(question.getStarterCode()));
    reviewDiv.appendElement("h3").text("Correct code");
    reviewDiv.appendChild(generateFileList(question.getCorrectCode()));
    reviewDiv.appendElement("h3").text("Test cases");
    reviewDiv.appendChild(generateTestCases(question.getTestCases()));
    return reviewDiv;
  }

  private static Element generateTestCases(List<CodeTestCase> testCases) {
    Element testCaseDiv = new Element("div");
    for (CodeTestCase testCase : testCases) {
      testCaseDiv.appendElement("h4").text(testCase.getName());
      testCaseDiv.appendChild(new Element("div").html(testCase.getDescription())
          .addClass("testCaseDescription"));

      if (testCase instanceof CodeQualityTestCase) {
        // Nothing to do, don't print anything
      } else if (testCase instanceof IoTestCase) {
        testCaseDiv.appendChild(generateIoTestCase((IoTestCase) testCase));
      } else if (testCase instanceof UnitTestCase) {
        testCaseDiv.appendChild(generateUnitTestCase((UnitTestCase) testCase));
      } else if (testCase instanceof CustomTestCase) {
        testCaseDiv
            .appendChild(generateCustomTestCase((CustomTestCase) testCase));
      }
    }
    return testCaseDiv;
  }

  private static Element generateIoTestCase(IoTestCase testCase) {
    Element div = new Element("div");
    div.appendElement("h5").text("Input");
    div.appendElement("code").text(testCase.getInput()).addClass("ioInput");
    div.appendElement("h5").text("Expected output");
    div.appendElement("pre").appendElement("code")
        .text(testCase.getExpectedOutput()).addClass("ioOutput");
    return div;
  }

  private static Element generateUnitTestCase(UnitTestCase testCase) {
    Element div = new Element("div");
    div.appendElement("h5").text("Test code");
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
    Element filesDiv = new Element("div");
    for (CodeFile file : fileList) {
      Element fileDiv = new Element("div");
      fileDiv.appendElement("h4").text(file.getFileName());
      fileDiv.appendElement("pre").appendElement("code").text(file.getContent())
          .addClass("codeFileField");
      filesDiv.appendChild(fileDiv);
    }
    return filesDiv;
  }
}
