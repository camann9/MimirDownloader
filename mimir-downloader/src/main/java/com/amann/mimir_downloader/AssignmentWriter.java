package com.amann.mimir_downloader;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = dbf.newDocumentBuilder();
    Document doc = Jsoup.builder.newDocument();
    doc.appendChild(generateHtml(assignment, doc));
    Util.printToFile(doc, target);
  }

  private static Element generateHtml(Assignment assignment, Document doc) {
    Element node = doc.createElement("html");
    node.appendChild(generateHead(assignment, doc));
    node.appendChild(generateBody(assignment, doc));
    return node;
  }

  private static Element generateHead(Assignment assignment, Document doc) {
    Element head = doc.createElement("head");
    Element title = doc.createElement("title");
    title.appendChild(doc.createTextNode(assignment.getName()));
    head.appendChild(title);
    return head;
  }

  private static Element generateBody(Assignment assignment, Document doc) {
    Element body = doc.createElement("body");
    
    Element title = doc.createElement("h1");
    title.setTextContent(assignment.getName());
    body.appendChild(title);
    
    Element description = doc.createElement("div");
    description.appendChild(doc.createTextNode(assignment.getName()));
    description.setAttribute("class", "description");
    description.appendChild(parse(assignment.getDescription()));
    body.appendChild(description);
    
    for (Question q : assignment.getQuestions()) {
      
    }
    return body;
  }

  private static String assignmentFileName(String name) {
    return name.replaceAll("[^a-zA-Z _0-9]", "").replaceAll(" ", "_")
        + ".html";
  }
  
  private static Element parse(String input) {
    Document doc = Jsoup.parseBodyFragment(input).body().;
    return doc.body();
  }
}
