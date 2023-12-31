package fi.johannes.chat.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.ServerWebSocket;

/**
 * Johannes on 20.3.2020.
 */
public class User {

  @JsonIgnore
  private ServerWebSocket socket;
  private MessageConsumer<?> consumer;
  private String roomId;
  private String userId;
  private String displayName;

  public User() {
  }

  public User(ServerWebSocket socket, String roomId, String userId, MessageConsumer<?> consumer, String displayName) {
    this.socket = socket;
    this.roomId = roomId;
    this.userId = userId;
    this.consumer = consumer;
    this.displayName = displayName;
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

  public MessageConsumer<?> getConsumer() {
    return consumer;
  }

  public void setConsumer(MessageConsumer<?> consumer) {
    this.consumer = consumer;
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

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public int hashCode() {
    return String.format("%s_%s", this.getRoomId(), this.getUserId()).hashCode();
  }
}
