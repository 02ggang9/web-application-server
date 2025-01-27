package webserver.http;

import java.util.Arrays;

public class DefaultHttp {

  private final HttpStartLine httpStartLine;
  private final HttpHeader httpHeader;

  public DefaultHttp(String[] httpLines) {
    this.httpStartLine = new HttpStartLine(httpLines[0]);
    this.httpHeader = new HttpHeader(Arrays.copyOfRange(httpLines, 1, httpLines.length));
  }

  public HttpStartLine getHttpStartLine() {
    return this.httpStartLine;
  }

  public HttpHeader getHttpHeader() {
    return this.httpHeader;
  }
}
