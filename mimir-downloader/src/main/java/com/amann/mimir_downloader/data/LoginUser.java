package com.amann.mimir_downloader.data;

import com.google.gson.annotations.SerializedName;

public final class LoginUser {
  @SerializedName("email")
  private String email;
  @SerializedName("password")
  private String password;

  public LoginUser(String email, String password) {
    this.setEmail(email);
    this.setPassword(password);
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
