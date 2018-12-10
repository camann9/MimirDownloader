package com.amann.mimir_downloader;

import java.io.File;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.amann.mimir_downloader.data.processed.Assignment;
import com.amann.mimir_downloader.data.processed.Course;
import com.amann.mimir_downloader.data.processed.Question;

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
    body.appendChild(new Element("h2").text(course.getName()));

    for (Assignment assignment : course.getAssignments()) {
      for (Question question : assignment.getQuestions()) {
        body.appendElement("h3").text(question.getTitle());
        body.appendElement("div").html(question.getDescription());
        Element links = body.appendElement("div");
        links.text("Links: ");
        links.appendElement("a").text("Mimir: " + assignment.getName()).attr("href",
            String.format(ASSIGNMENT_URL_FORMAT, assignment.getId()));
      }
    }
  }
}
