package fi.johannes.chat.types;

/**
 * Johannes on 20.3.2020.
 */
public class InternalMessage implements MessageEvent {

  private String senderId;
  private String senderDisplayName;
  private String message;

  public InternalMessage(String senderId, String senderDisplayName, String message) {
    this.senderId = senderId;
    this.senderDisplayName = senderDisplayName;
    this.message = message;
  }

  public InternalMessage() {
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

  public String getSenderDisplayName() {
    return senderDisplayName;
  }

  public void setSenderDisplayName(String senderDisplayName) {
    this.senderDisplayName = senderDisplayName;
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
