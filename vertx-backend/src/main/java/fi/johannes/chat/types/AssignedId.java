package fi.johannes.chat.types;

/**
 * Johannes on 22.3.2020.
 */
public class AssignedId extends IdMessage {


  public AssignedId(String id, String displayName) {
    super(id, displayName);
  }

  @Override
  public String getType() {
    return "assigned_id";
  }
}
