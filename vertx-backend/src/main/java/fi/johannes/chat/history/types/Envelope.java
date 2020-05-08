package fi.johannes.chat.history.types;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Johannes on 6.5.2020.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envelope<T> {

  private Meta meta;
  private Room room;
  private Sender sender;
  private Message<T> message;

  public JsonObject json() {
    return JsonObject.mapFrom(this);
  }
  public String jsonStr() {
    return json().encode();
  }
}

