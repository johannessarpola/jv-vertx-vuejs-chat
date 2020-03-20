package fi.bilot.websocket.chat.types;

import io.vertx.core.json.JsonObject;

/**
 * Johannes on 20.3.2020.
 */
public class Message {

  private User user;
  private String message;

  public Message(User user, String message) {
    this.user = user;
    this.message = message;
  }

  public Message() {
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String json() {
    return JsonObject.mapFrom(this).encode();
  }
}
