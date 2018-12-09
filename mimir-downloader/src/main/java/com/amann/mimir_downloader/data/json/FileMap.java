package com.amann.mimir_downloader.data.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class FileMap {
  @SerializedName("__pairs__")
  List<FileMapEntry> entries;

  public List<FileMapEntry> getEntries() {
    return entries;
  } 
}
