package com.amann.mimir_downloader.data.json;

public class RawTestCase {
  String name;
  // HTML description
  String description;
  // E.g. "unit"
  String testType;
  // Code or text input for I/O test cases
  String input;
  // Output of correct code
  String perfectOutput;
  // Expected output for I/O test cases
  String expectedOutput;
}
