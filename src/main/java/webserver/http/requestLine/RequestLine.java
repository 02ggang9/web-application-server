package webserver.http.requestLine;

import java.util.Map;
import webserver.http.HttpStatus;

public interface RequestLine {

  HttpStatus getMethod();

  String getUrl();

  Map<String, String> getQueries();

  String getVersion();

}
