package com.amann.mimir_downloader.data.processed;

public final class IoTestCase extends CodeTestCase {
  private String input;
  private String expectedOutput;

  public IoTestCase(String name, String description, String input, String expectedOutput) {
    super(name, description);
    this.input = input;
    this.expectedOutput = expectedOutput;
  }

  public String getInput() {
    return input;
  }

  public String getExpectedOutput() {
    return expectedOutput;
  }
}
