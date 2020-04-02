package fi.bilot.websocket.chat;

import fi.bilot.websocket.chat.types.Room;
import fi.bilot.websocket.chat.types.ToJson;
import fi.bilot.websocket.chat.types.User;
import fi.bilot.websocket.chat.types.UserJoined;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 *
 */
public class ChatRooms {

  private Map<String, Room> rooms;
  private Map<String, User> users;
  private String basePath = "room";

  public ChatRooms() {
    this.rooms = new ConcurrentHashMap<>();
    this.users = new ConcurrentHashMap<>();
  }

  public String roomPath(String roomId) {
    return String.format("%s_%s", basePath, roomId);
  }

  private void addUser(User user) {
    users.put(user.getUserId(), user);
  }

  private void removeUser(User user) {
    users.remove(user.getUserId());
  }

  public Room joinRoom(String roomId, User user) {
    addUser(user);
    if (rooms.containsKey(roomId)) {
      Room room = rooms.get(roomId);
      room.addUser(user.getUserId());
      return room;
    } else {
      Room room = new Room(roomId, roomPath(roomId));
      room.addUser(user.getUserId());
      rooms.put(roomId, room);
      return room;
    }
  }

  public void unregisterUser(String roomId, User user) {
    if (rooms.containsKey(roomId)) {
      rooms.get(roomId).removeUser(user.getUserId());
    }
    removeUser(user);
  }

}
