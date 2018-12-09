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
import com.amann.mimir_downloader.data.processed.CodeReviewQuestion;
import com.amann.mimir_downloader.data.processed.CodingQuestion;
import com.amann.mimir_downloader.data.processed.FileUploadQuestion;
import com.amann.mimir_downloader.data.processed.LongAnswerQuestion;
import com.amann.mimir_downloader.data.processed.MultipleChoiceQuestion;
import com.amann.mimir_downloader.data.processed.Question;
import com.amann.mimir_downloader.data.processed.ShortAnswerQuestion;

public final class AssignmentWriter {
  public static void writeAssignment(Assignment assignment, File folder,
      boolean overwrite) throws Exception {
    File target = new File(folder, assignmentFileName(assignment.getName()));
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
    }else if (question instanceof FileUploadQuestion) {
      container.appendChild(new Element("div").addClass("fileAnswerField"));
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
    return reviewDiv;
  }

  private static Element generateFileList(List<CodeFile> fileList) {
    Element filesDiv = new Element("div");
    for (CodeFile file : fileList) {
      Element fileDiv = new Element("div");
      fileDiv.appendElement("h4").text(file.getFileName());
      fileDiv.appendElement("div").text(file.getContent())
          .addClass("codeField");
      filesDiv.appendChild(fileDiv);
    }
    return filesDiv;
  }

  private static String assignmentFileName(String name) {
    return name.replaceAll("[^a-zA-Z _0-9]", "").replaceAll(" ", "_") + ".html";
  }
}
