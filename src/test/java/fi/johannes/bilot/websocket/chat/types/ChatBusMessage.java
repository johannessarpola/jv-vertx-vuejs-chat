package fi.johannes.bilot.websocket.chat.types;

import java.util.Objects;

/**
 * Johannes on 2.4.2020.
 */
public class ChatBusMessage<T> {
  private String address;
  private String sender;
  private T message;

  public ChatBusMessage(String address, String sender, T message) {
    this.address = address;
    this.sender = sender;
    this.message = message;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public T getMessage() {
    return message;
  }

  public void setMessage(T message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChatBusMessage<?> that = (ChatBusMessage<?>) o;
    return Objects.equals(getAddress(), that.getAddress()) &&
      Objects.equals(getSender(), that.getSender()) &&
      Objects.equals(getMessage(), that.getMessage());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getAddress(), getSender(), getMessage());
  }
}
