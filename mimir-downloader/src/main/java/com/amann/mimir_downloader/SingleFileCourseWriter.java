package com.amann.mimir_downloader;

import java.io.File;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.amann.mimir_downloader.data.processed.Assignment;
import com.amann.mimir_downloader.data.processed.CheckboxQuestion;
import com.amann.mimir_downloader.data.processed.CodeReviewQuestion;
import com.amann.mimir_downloader.data.processed.CodingQuestion;
import com.amann.mimir_downloader.data.processed.Course;
import com.amann.mimir_downloader.data.processed.FileUploadQuestion;
import com.amann.mimir_downloader.data.processed.LongAnswerQuestion;
import com.amann.mimir_downloader.data.processed.MultipleChoiceQuestion;
import com.amann.mimir_downloader.data.processed.Question;
import com.amann.mimir_downloader.data.processed.ShortAnswerQuestion;

public final class SingleFileCourseWriter {
  final static String ASSIGNMENT_URL_FORMAT = "https://class.mimir.io/assignments/%s/edit";

  public static void writeCourse(Course course, File target,
      boolean overwriteFiles) throws Exception {
    Util.checkShouldOverwrite(target, overwriteFiles);
    Document doc = Document.createShell("fake_url");
    generateHtml(course, doc);
    Util.printToFile(doc, target);
  }

  private static void generateHtml(Course course, Document doc) {
    doc.title(course.getName());
    generateBody(course, doc.body());
  }

  private static void generateBody(Course course, Element body) {
    body.appendChild(new Element("h1").text(course.getName()));

    for (Assignment assignment : course.getAssignments()) {
      for (Question question : assignment.getQuestions()) {
        generateQuestion(body, assignment, question);
      }
    }
  }

  private static void generateQuestion(Element parent, Assignment assignment,
      Question question) {
    parent.appendElement("h2").text(question.getTitle());
    parent.appendElement("div").html(question.getDescription());
    AssignmentWriter.generateShortQuestion(question, parent);
    Element links = parent.appendElement("div");
    links.text("Links: ");
    links.appendElement("a").text("Mimir: " + assignment.getName()).attr("href",
        String.format(ASSIGNMENT_URL_FORMAT, assignment.getId()));
  }
}
