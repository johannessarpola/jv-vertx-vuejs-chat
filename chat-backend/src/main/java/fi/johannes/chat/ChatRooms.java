package fi.johannes.chat;

import fi.johannes.chat.forwarders.ChatRoomsForwarder;
import fi.johannes.chat.types.Room;
import fi.johannes.chat.types.User;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ChatRooms {
  protected static final Logger logger = LoggerFactory.getLogger(ChatRooms.class);

  private final ChatRoomsForwarder broker;
  private Map<String, Room> rooms;
  private Map<String, User> usersById;
  private Map<String, User> usersByDisplay;

  public ChatRooms(EventBus eventBus) {
    this.broker = new ChatRoomsForwarder(eventBus);
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
      logger.info(String.format("User %s-%s joined existing room %s", user.getUserId(), user.getDisplayName(), room.getId()));
      room.addUser(user.getUserId());
      return room;
    } else {
      logger.info(String.format("User %s-%s joined new room %s", user.getUserId(), user.getDisplayName(), room.getId()));
      room.addUser(user.getUserId());
      rooms.put(room.getId(), room);
      broker.newRoom(room.getAddress());
      return room;
    }
  }

  public void removeUser(String roomId, User user) {
    if (rooms.containsKey(roomId)) {
      rooms.get(roomId).removeUser(user.getUserId());
      if (rooms.get(roomId).getUsers().isEmpty()) {
        Room room = rooms.get(roomId);
        rooms.remove(roomId);
        this.broker.removedRoom(room.getAddress());
      }
    }
    removeUser(user);
  }

}
