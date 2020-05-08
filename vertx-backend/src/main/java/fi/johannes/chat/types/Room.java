package fi.johannes.chat.types;

import io.vertx.core.impl.ConcurrentHashSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Johannes on 26.3.2020.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

  private static String basePath = "room";
  private String id;
  private String address;
  private Set<String> users;

  public Room(String id) {
    this.id = id;
    this.address = defaultRoomPath(id);
    this.users = new HashSet<>();
  }

  public void removeUser(String user) {
    this.users.remove(user);
  }

  public void addUser(String user) {
    this.users.add(user);
  }

  public String defaultRoomPath(String roomId) {
    return String.format("%s_%s", basePath, roomId);
  }

}
