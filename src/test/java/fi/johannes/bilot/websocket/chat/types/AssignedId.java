package fi.johannes.bilot.websocket.chat.types;

/**
 * Johannes on 22.3.2020.
 */
public class AssignedId extends IdMessage {


  public AssignedId(String id) {
    super(id);
  }

  @Override
  public String getType() {
    return "assigned_id";
  }
}
