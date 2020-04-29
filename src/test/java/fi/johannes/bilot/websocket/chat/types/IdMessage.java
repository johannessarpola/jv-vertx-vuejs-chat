package fi.johannes.bilot.websocket.chat.types;

/**
 * Johannes on 22.3.2020.
 */
public abstract class IdMessage implements MessageEvent {

  public IdMessage(String id) {
    this.id = id;
  }

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean recipientFilter(User recipient) {
    // Don't need to filter by default
    return true;
  }
}
