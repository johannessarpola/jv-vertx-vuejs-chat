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
public class ChatMediator {

  private Map<String, Room> rooms = new ConcurrentHashMap<>();
  private Map<String, User> users = new ConcurrentHashMap<>();
  private EventBus eventBus;
  private String basePath = "room";

  public ChatMediator(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  private String roomPath(String roomId) {
    return String.format("%s_%s", basePath, roomId);
  }

  public void registerUser(String roomId, User user) {
    addUser(user);
    this.rooms.get(roomId).addUser(user.getUserId());
  }

  private void addUser(User user) {
    users.put(user.getUserId(), user);
  }

  private void removeUser(User user) {
    users.remove(user.getUserId());
  }

  private void registerConsumer(Room room, User user) {
    this.eventBus.consumer(room.getAddress()).handler((msg) -> {
      System.out.println("consumer");
      System.out.println(msg.body().toString());
      room.getUsers().forEach((uid) -> {
        if (!uid.equals(user.getUserId())) {
          users.get(uid).getSocket().writeTextMessage(msg.body().toString());
        }
      });
    });
  }

  public void joinRoom(String roomId, User user) {
    addUser(user);
    Room room;
    if (rooms.containsKey(roomId)) {
      room = rooms.get(roomId);
      room.addUser(user.getUserId());
    } else {
      room = new Room(roomId, roomPath(roomId));
      room.addUser(user.getUserId());
      rooms.put(roomId, room);
      registerConsumer(room, user);
    }
  }

  public void unregisterUser(String roomId, User user) {
    if (rooms.containsKey(roomId)) {
      rooms.get(roomId).removeUser(user.getUserId());
    }
    removeUser(user);
  }

  public void send(String roomId, ToJson jsonMessage) {
    Room room = rooms.getOrDefault(roomId, null);
    if (room != null) {
      this.eventBus.send(room.getAddress(), jsonMessage.json());
    }
  }

}
