package com.amann.mimir_downloader.data.processed;

public final class CustomTestCase extends CodeTestCase {
  private String testBashScript;

  public CustomTestCase(String name, String description, String testBashScript) {
    super(name, description);
    this.testBashScript = testBashScript;
  }

  public String getTestBashScript() {
    return testBashScript;
  }
}
