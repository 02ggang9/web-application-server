package webserver.http;

import java.util.HashMap;
import java.util.Map;

public class HttpStartLine {

  private final String method;
  private final String path;
  private final Map<String, String> queries = new HashMap<>();
  private final String version;

  public HttpStartLine(String startLine) {
    String[] splitedStartLine = startLine.split(" ");
    this.method = splitedStartLine[0];

    if (splitedStartLine[1].split("\\?").length == 1) { // Query가 없고, path 뿐인 경우
      this.path = splitedStartLine[1];
    } else {
      String[] pathAndQuery = splitedStartLine[1].split("\\?");
      this.path = pathAndQuery[0];

      String[] splitQueries = pathAndQuery[1].split("&");
      for (int i = 0; i < splitQueries.length; i++) {
        String[] keyAndValue = splitQueries[i].split("=");
        String key = keyAndValue[0];
        String value = keyAndValue[1];

        queries.put(key, value);
      }
    }

    this.version = splitedStartLine[2];
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public String getVersion() {
    return version;
  }

  public Map<String, String> getQueries() {
    return queries;
  }
}
