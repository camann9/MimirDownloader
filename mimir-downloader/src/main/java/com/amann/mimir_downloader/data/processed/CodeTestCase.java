package com.amann.mimir_downloader.data.processed;

public class CodeTestCase {
  String name;
  String description;
  
  public CodeTestCase(String name, String description) {
    super();
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
