package fi.bilot.websocket.chat.types;

import io.vertx.core.json.JsonObject;

import java.util.Set;

/**
 * Johannes on 20.3.2020.
 */
public class ChatMessage implements MessageEvent {

  private String senderId;
  private String message;

  public ChatMessage(String user, String message) {
    this.senderId = user;
    this.message = message;
  }

  public ChatMessage() {
  }

  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String getType() {
    return "chat";
  }

  @Override
  public boolean recipientFilter(User recipient) {
    return !recipient.getUserId().equals(this.senderId);
  }
}
