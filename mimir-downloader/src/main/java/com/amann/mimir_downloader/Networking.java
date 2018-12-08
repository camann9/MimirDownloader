package com.amann.mimir_downloader;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.Joiner;
import com.google.gson.Gson;

import json.Config;
import json.LoginUser;

public final class Networking {
  final static String USER_SESSION_URL = "https://class.mimir.io/lms/user_sessions";
  final static String SESSION_TOKEN_COOKIE = "user_session_token";
  final static String SESSION_ID_COOKIE = "user_session_id";
  public final static HttpRequestFactory REQUEST_FACTORY = new NetHttpTransport()
      .createRequestFactory();
  final static HttpBackOffUnsuccessfulResponseHandler BACKOFF = new HttpBackOffUnsuccessfulResponseHandler(
      new ExponentialBackOff.Builder().setInitialIntervalMillis(500)
          .setMaxElapsedTimeMillis(30000).setMultiplier(1.5)
          .setRandomizationFactor(0.5).build());
  final static Gson GSON = new Gson();

  private static boolean updateSession(HttpResponse response, Config config) {
    List<String> cookieHeaders = (List<String>) response.getHeaders()
        .get("Set-Cookie");
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
        .filter(cookie -> cookie.getName().equals(SESSION_TOKEN_COOKIE))
        .findAny();

    // If the login failed the cookies will not be present so we return false.
    // Sadly we still get a 200 response...
    if (!idCookie.isPresent() || !tokenCookie.isPresent()) {
      return false;
    }
    config.setSessionId(idCookie.get().getValue());
    config.setSessionToken(tokenCookie.get().getValue());
    return true;
  }

  public static HttpHeaders createAuthHeaders(Config config) {
    CookieManager cookieManager = new CookieManager();
    createAndAddCookie(cookieManager, SESSION_TOKEN_COOKIE,
        config.getSessionToken());
    createAndAddCookie(cookieManager, SESSION_ID_COOKIE, config.getSessionId());
    String cookieString = Joiner.on(';')
        .join(cookieManager.getCookieStore().getCookies());
    return new HttpHeaders().setCookie(cookieString);
  }

  public static String executeAuthedRequest(String url, Config config)
      throws IOException {
    HttpRequest request = REQUEST_FACTORY.buildGetRequest(new GenericUrl(url))
        .setHeaders(Networking.createAuthHeaders(config))
        .setUnsuccessfulResponseHandler(BACKOFF);
    return request.execute().parseAsString();
  }

  private static void createAndAddCookie(CookieManager cookieManager,
      String name, String value) {
    HttpCookie cookie = new HttpCookie(name, value);
    cookie.setVersion(0);
    cookieManager.getCookieStore().add(null, cookie);
  }

  public static boolean createSession(Config config, String username,
      String password) throws IOException {
    LoginUser user = new LoginUser(username, password);
    ByteArrayContent content = new ByteArrayContent("application/json",
        GSON.toJson(user).getBytes());
    try {
      // Triggering login works by sending a POST request with the right data to
      // the USER_SESSION endpoint
      HttpResponse response = REQUEST_FACTORY
          .buildPostRequest(new GenericUrl(USER_SESSION_URL), content)
          .setUnsuccessfulResponseHandler(BACKOFF)
          .execute();
      return updateSession(response, config);
    } catch (HttpResponseException ex) {
      // If we get "unauthorized" then username/password was incorrect
      if (ex.getStatusCode() == 401) {
        return false;
      } else {
        throw ex;
      }
    }
  }

  public static boolean verifySession(Config config) throws IOException {
    if (config.getSessionId() == null || config.getSessionToken() == null) {
      return false;
    }
    try {
      // Try to execute a request to user_sessions with current credentials.
      // If that fails we need to refresh.
      executeAuthedRequest(USER_SESSION_URL, config);
      return true;
    } catch (HttpResponseException ex) {
      // If we get unauthorized we need to update
      if (ex.getStatusCode() == 401) {
        return false;
      } else {
        throw ex;
      }
    }
  }
}
