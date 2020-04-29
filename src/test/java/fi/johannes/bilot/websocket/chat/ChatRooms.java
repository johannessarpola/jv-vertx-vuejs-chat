package fi.johannes.bilot.websocket.chat;

import fi.johannes.bilot.websocket.chat.types.Room;
import fi.johannes.bilot.websocket.chat.types.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ChatRooms {

  private Map<String, Room> rooms;
  private Map<String, User> users;

  public ChatRooms() {
    this.rooms = new ConcurrentHashMap<>();
    this.users = new ConcurrentHashMap<>();
  }

  private void addUser(User user) {
    users.put(user.getUserId(), user);
  }

  private void removeUser(User user) {
    users.remove(user.getUserId());
  }

  public Room joinRoom(Room room, User user) {
    addUser(user);
    if (rooms.containsKey(room.getId())) {
      room.addUser(user.getUserId());
      return room;
    } else {
      room.addUser(user.getUserId());
      rooms.put(room.getId(), room);
      return room;
    }
  }

  public void removeUser(String roomId, User user) {
    if (rooms.containsKey(roomId)) {
      rooms.get(roomId).removeUser(user.getUserId());
    }
    removeUser(user);
  }

}
