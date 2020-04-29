package fi.johannes.chat.types;

/**
 * Johannes on 22.3.2020.
 */
public abstract class IdMessage implements MessageEvent {

  public IdMessage(String id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }
  private String id;
  private String displayName;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public boolean recipientFilter(User recipient) {
    // Don't need to filter by default
    return true;
  }
}
