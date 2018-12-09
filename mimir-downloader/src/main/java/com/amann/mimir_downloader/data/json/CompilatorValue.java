package com.amann.mimir_downloader.data.json;

import java.util.List;

public final class CompilatorValue {
  FileSet perfectWorkingState;
  FileSet skeletonWorkingState;
  List<RawTestCase> testCases;

  public FileSet getPerfectWorkingState() {
    return perfectWorkingState;
  }
  public FileSet getSkeletonWorkingState() {
    return skeletonWorkingState;
  }
  public List<RawTestCase> getTestCases() {
    return testCases;
  }
}
