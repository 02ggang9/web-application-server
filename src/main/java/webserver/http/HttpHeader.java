package webserver.http;

import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

  private final Map<String, String> httpHeaders = new HashMap<>();

  public HttpHeader(String[] lines) {
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      String[] splitedLine = line.split(":");
      String headerKey = splitedLine[0];
      String headerValue = splitedLine[1];

      this.httpHeaders.put(headerKey, headerValue);
    }
  }

  public String getHeaderValue(String headerKey) {
    return this.httpHeaders.get(headerKey);
  }

}
