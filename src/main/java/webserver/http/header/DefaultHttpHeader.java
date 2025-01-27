package webserver.http.header;

import java.util.Map;

public class DefaultHttpHeader implements HttpHeader {

  private final Map<String, String> httpHeaders;

  public DefaultHttpHeader(Map<String, String> httpHeaders) {
    this.httpHeaders = httpHeaders;
  }

  @Override
  public String getValue(String key) {
    return httpHeaders.get(key);
  }

}
