package fi.bilot.websocket.chat;

import fi.bilot.websocket.chat.types.User;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class ChatMediator {

  private Map<String, Set<User>> roomsUsers = new HashMap<>();
  private EventBus eventBus;
  private String basePath;

  public ChatMediator(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  private String roomPath(String roomId) {
    return String.format("%s_%s", basePath, roomId);
  }

  public void registerUser(String roomId, User user) {
    this.roomsUsers.get(roomId).add(user);
  }

  public void unregisterUser(String roomId, User user) {
    this.roomsUsers.get(roomId).remove(user);
    if (this.roomsUsers.get(roomId).isEmpty()) {
      this.roomsUsers.remove(roomId);
    }
  }

  public void newRoom(String roomId, User user) {
    String newPath = roomPath(roomId);
    Set<User> s = new HashSet<>();
    s.add(user);
    this.roomsUsers.put(roomId, s);
    this.eventBus.consumer(newPath).handler((msg) -> {
      System.out.println("consumer");
      System.out.println(msg.body().toString());
      this.roomsUsers.get(roomId).forEach((u) -> {
        if(u.getUserId() != user.getUserId()) {
          u.getSocket().writeTextMessage(msg.body().toString());
        }
      });
    });
  }

  public void onMessage(String roomId, Buffer msgb) {
    this.eventBus.send(roomPath(roomId), msgb);
  }
}
