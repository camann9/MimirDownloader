package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import com.amann.mimir_downloader.data.processed.Assignment;
import com.amann.mimir_downloader.data.processed.Question;

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
    
    body.appendChild(new Element("div").html(assignment.getDescription()).addClass("description"));

    for (Question q : assignment.getQuestions()) {
      
    }
  }

  private static String assignmentFileName(String name) {
    return name.replaceAll("[^a-zA-Z _0-9]", "").replaceAll(" ", "_")
        + ".html";
  }
}
