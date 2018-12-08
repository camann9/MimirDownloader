package json;

import com.google.gson.annotations.SerializedName;

public final class MimirUser {
  @SerializedName("notificationToken")
  private String notificationToken;

  public String getNotificationToken() {
    return notificationToken;
  }

  public void setNotificationToken(String notificationToken) {
    this.notificationToken = notificationToken;
  }
}
