package fi.johannes.chat;

import fi.johannes.chat.types.Room;
import fi.johannes.chat.types.User;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ChatRooms {

  private Map<String, Room> rooms;
  private Map<String, User> usersById;
  private Map<String, User> usersByDisplay;

  public ChatRooms() {
    this.rooms = new ConcurrentHashMap<>();
    this.usersById = new ConcurrentHashMap<>();
    this.usersByDisplay = new ConcurrentHashMap<>();
  }

  private void addUser(User user) {
    usersById.put(user.getUserId(), user);
    usersByDisplay.put(user.getDisplayName(), user);
  }

  private void removeUser(User user) {
    usersById.remove(user.getUserId());
    usersByDisplay.remove(user.getDisplayName());
  }

  public Set<String> getUserIds() {
    return this.usersById.keySet();
  }

  public Set<String> getUserDisplayNames() {
    return this.usersByDisplay.keySet();
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
