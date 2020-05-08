package fi.johannes.chat.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Johannes on 22.3.2020.
 */
public class AssignedId extends IdMessage {

  public AssignedId(String id, String displayName, String roomId) {
    super(id, displayName, roomId);
  }

  @Override
  public String getType() {
    return "assigned_id";
  }
}
