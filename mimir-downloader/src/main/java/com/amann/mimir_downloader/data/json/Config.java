package com.amann.mimir_downloader.data.json;

import com.google.gson.annotations.SerializedName;

public final class Config {
  @SerializedName("user_session_token")
  private String sessionToken;
  @SerializedName("user_session_id")
  private String sessionId;

  public String getSessionToken() {
    return sessionToken;
  }

  public void setSessionToken(String sessionToken) {
    this.sessionToken = sessionToken;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
}
