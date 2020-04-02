package fi.bilot.websocket.chat.types;

import io.vertx.core.impl.ConcurrentHashSet;

import java.util.Set;

/**
 * Johannes on 26.3.2020.
 */
public class Room {

  private String id;
  private String address;
  private Set<String> users;

  public Room(String id, String address) {
    this.id = id;
    this.address = address;
    this.users = new ConcurrentHashSet<>();
  }

  public String getId() {
    return id;
  }

  public Set<String> getUsers() {
    return users;
  }

  public void removeUser(String user) {
    this.users.remove(user);
  }

  public void addUser(String user) {
    this.users.add(user);
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}