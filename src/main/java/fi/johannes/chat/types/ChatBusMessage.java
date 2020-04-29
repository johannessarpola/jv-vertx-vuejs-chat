package fi.johannes.chat.types;

import java.util.Objects;

/**
 * Johannes on 2.4.2020.
 */
public class ChatBusMessage<T> {
  private String address;
  private String senderId;
  private T message;

  public ChatBusMessage(String address, String senderId, T message) {
    this.address = address;
    this.senderId = senderId;
    this.message = message;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
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
      Objects.equals(getSenderId(), that.getSenderId()) &&
      Objects.equals(getMessage(), that.getMessage());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getAddress(), getSenderId(), getMessage());
  }
}
