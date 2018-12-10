package com.amann.mimir_downloader;

import java.io.IOException;
import java.time.Instant;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DateTypeAdapter extends TypeAdapter<Instant> {
  @Override
  public void write(JsonWriter out, Instant value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(value.toString());
    }
  }

  @Override
  public Instant read(JsonReader in) throws IOException {
    if (in != null) {
      return Instant.parse(in.nextString());
    } else {
      return null;
    }
  }
}