package webserver.http.requestLine;

import java.util.Map;
import webserver.http.HttpStatus;

public class DefaultRequestLine implements RequestLine {

  private final HttpStatus httpStatus;
  private final String url;
  private final Map<String, String> queries;
  private final String version;

  public DefaultRequestLine(final HttpStatus httpStatus, final String url, final Map<String, String> queries, final String version) {
    this.httpStatus = httpStatus;
    this.url = url;
    this.queries = queries;
    this.version = version;
  }

  @Override
  public HttpStatus getMethod() {
    return this.httpStatus;
  }

  @Override
  public String getUrl() {
    return this.url;
  }

  @Override
  public Map<String, String> getQueries() {
    return this.queries;
  }

  @Override
  public String getVersion() {
    return this.version;
  }
}
