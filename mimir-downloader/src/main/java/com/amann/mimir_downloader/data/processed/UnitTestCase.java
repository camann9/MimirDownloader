package com.amann.mimir_downloader.data.processed;

public final class UnitTestCase extends CodeTestCase {
  private String testCode;

  public UnitTestCase(String name, String description, String testCode) {
    super(name, description);
    this.testCode = testCode;
  }

  public String getTestCode() {
    return testCode;
  }
}
