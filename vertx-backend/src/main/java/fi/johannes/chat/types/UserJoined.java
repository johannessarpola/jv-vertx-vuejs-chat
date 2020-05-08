package fi.johannes.chat.types;

/**
 * Johannes on 22.3.2020.
 */
public class UserJoined extends IdMessage {

  public UserJoined(String id, String displayName, String roomId) {
    super(id, displayName, roomId);
  }

  @Override
  public String getType() {
    return "user_joined";
  }

}
