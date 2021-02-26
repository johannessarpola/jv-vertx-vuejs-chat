package fi.johannes.chat.types;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Johannes on 7.5.2020.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomMessage {

  private String roomAddress;
  private RoomAction action;

  public enum RoomAction {
    New,
    Removed
  }

  public String jsonStr() {
    return json().encode();
  }
  public JsonObject json() {
    return JsonObject.mapFrom(this);
  }
}


