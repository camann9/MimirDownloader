package com.amann.mimir_downloader.data.processed;

public final class CodeFile {
  private String fileName;
  private String content;

  public CodeFile(String fileName, String content) {
    this.fileName = fileName;
    this.content = content;
  }

  public String getFileName() {
    return fileName;
  }

  public String getContent() {
    return content;
  }  
}
