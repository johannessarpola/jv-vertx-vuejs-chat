package fi.bilot.websocket.chat.types;

/**
 *
 */
public class InboundMessage {

  private String date;
  private String message;

  public InboundMessage(String date, String message) {
    this.date = date;
    this.message = message;
  }

  public InboundMessage() {
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
