package webserver.http.body;

import java.util.Map;

public interface HttpBody {

  Map<String, String> getBody();

  String value(String key);

}
