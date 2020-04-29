package fi.johannes.bilot.websocket.chat.types;

/**
 * Johannes on 22.3.2020.
 */
public class UserJoined extends IdMessage {

  public UserJoined(String id) {
    super(id);
  }

  @Override
  public String getType() {
    return "user_joined";
  }

}
