package fi.johannes.chat.types;

import io.vertx.core.json.JsonObject;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Johannes on 22.3.2020.
 */
public interface MessageEvent extends ToJson {

  String getType();
  String getRoomId();
  default String jsonStr() {
    return JsonObject.mapFrom(this).encode();
  }
  default JsonObject json() {
    return JsonObject.mapFrom(this);
  }
}
