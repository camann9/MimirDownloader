package com.amann.mimir_downloader;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.amann.mimir_downloader.data.Config;
import com.amann.mimir_downloader.data.LoginUser;
import com.amann.mimir_downloader.data.MimirUser;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Joiner;
import com.google.gson.Gson;

public final class Login {
  final static GenericUrl USER_SESSION_URL =
      new GenericUrl("https://class.mimir.io/lms/user_sessions");
  final static String SESSION_TOKEN_COOKIE = "user_session_token";
  final static String SESSION_ID_COOKIE = "user_session_id";
  final static HttpRequestFactory requestFactory =
      new NetHttpTransport().createRequestFactory(
          (HttpRequest request) -> {
            request.setParser(new JsonObjectParser(new GsonFactory()));
          });
  final static Gson GSON = new Gson();

  public static void getOrUpdateSession(Config config) throws IOException {
    if (config.getSessionToken() != null && config.getSessionId() != null) {
      // Try to use session token on user_sessions. If that fails we need
      // to refresh.
      HttpRequest request = requestFactory
          .buildGetRequest(USER_SESSION_URL)
          .setHeaders(createHeaders(config));
      try {
        MimirUser user = request.execute().parseAs(MimirUser.class);
        System.out.println(user.getNotificationToken());
      } catch(HttpResponseException ex) {
        // If we get unauthorized we need to update
        if (ex.getStatusCode() == 401) {
          updateSession(config);
        } else {
          throw ex;
        }
      }
    } else {
      updateSession(config);
    }
  }
  
  private static void updateSession(Config config) throws IOException {
    while (true) {
      LoginUser user = getLoginUser();
      ByteArrayContent content =
          new ByteArrayContent("application/json", GSON.toJson(user).getBytes());
      try {
        // Triggering login works by sending a POST request with the right data to the
        // USER_SESSION endpoint
        HttpResponse response =
            requestFactory.buildPostRequest(USER_SESSION_URL, content).execute();
        if (updateSession(response, config)) {
          return;
        }
        else {
          System.out.println("Incorrect user name or password");
        }
      } catch(HttpResponseException ex) {
        // If we get "unauthorized" then username/password was incorrect
        if (ex.getStatusCode() == 401) {
          System.out.println("Incorrect user name or password");
        } else {
          throw ex;
        }
      }
    }
  }

  private static boolean updateSession(HttpResponse response, Config config) {
    List<String> cookieHeaders =
        (List<String>) response.getHeaders().get("Set-Cookie");
    if (cookieHeaders == null) {
      return false;
    }

    List<HttpCookie> allCookies = new ArrayList<HttpCookie>();
    for (String s : cookieHeaders) {
      List<HttpCookie> cookies = HttpCookie.parse(s);
      allCookies.addAll(cookies);
    }
    Optional<HttpCookie> idCookie = allCookies.stream()
        .filter(cookie -> cookie.getName().equals(SESSION_ID_COOKIE)).findAny();
    Optional<HttpCookie> tokenCookie = allCookies.stream()
        .filter(cookie -> cookie.getName().equals(SESSION_TOKEN_COOKIE)).findAny();

    if (!idCookie.isPresent() || !tokenCookie.isPresent()) {
      return false;
    }
    config.setSessionId(idCookie.get().getValue());
    config.setSessionToken(tokenCookie.get().getValue());
    return true;
  }

  private static LoginUser getLoginUser() {
    Scanner s = new Scanner(System.in);
    System.out.println("Enter your mimir email");
    String email = s.nextLine();
    System.out.println("Enter your mimir password");
    String password = s.nextLine();
    LoginUser user = new LoginUser(email, password);
    return user;
  }

  public static HttpHeaders createHeaders(Config config) {
    CookieManager cookieManager = new CookieManager();
    createAndAddCookie(cookieManager, SESSION_TOKEN_COOKIE, config.getSessionToken());
    createAndAddCookie(cookieManager, SESSION_ID_COOKIE, config.getSessionId());
    String cookieString = Joiner.on(';').join(cookieManager.getCookieStore().getCookies());
    return new HttpHeaders().setCookie(cookieString);
  }

  private static void createAndAddCookie(
      CookieManager cookieManager, String name, String value) {
    HttpCookie cookie = new HttpCookie(name, value);
    cookie.setVersion(0);
    cookieManager.getCookieStore().add(null, cookie);
  }
  
}
