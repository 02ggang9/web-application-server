package webserver.http.body;

import java.util.Map;

public class DefaultHttpBody implements HttpBody {

  private final Map<String, String> bodies;

  public DefaultHttpBody(Map<String, String> bodies) {
    this.bodies = bodies;
  }

  @Override
  public Map<String, String> getBody() {
    return this.bodies;
  }

  @Override
  public String value(String key) {
    return this.bodies.get(key);
  }
}
