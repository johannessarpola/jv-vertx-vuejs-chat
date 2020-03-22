package fi.bilot.websocket.chat.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.core.http.ServerWebSocket;

/**
 * Johannes on 20.3.2020.
 */
public class User {

  @JsonIgnore
  private ServerWebSocket socket;
  private String roomId;
  private String userId;

  public User() {
  }

  public User(ServerWebSocket socket, String roomId, String userId) {
    this.socket = socket;
    this.roomId = roomId;
    this.userId = userId;
  }

  public ServerWebSocket getSocket() {
    return socket;
  }

  public String getRoomId() {
    return roomId;
  }

  public String getUserId() {
    return userId;
  }

  public void setSocket(ServerWebSocket socket) {
    this.socket = socket;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public int hashCode() {
    return String.format("%s_%s", this.getRoomId(), this.getUserId()).hashCode();
  }
}
