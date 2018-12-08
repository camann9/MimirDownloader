package com.amann.mimir_downloader.data.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CompilatorMap {
  @SerializedName("__pairs__")
  List<CompilatorEntry> entries; 
}
