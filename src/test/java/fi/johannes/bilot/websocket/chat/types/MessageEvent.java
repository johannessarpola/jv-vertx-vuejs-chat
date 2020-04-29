package fi.johannes.bilot.websocket.chat.types;

import io.vertx.core.json.JsonObject;

/**
 * Johannes on 22.3.2020.
 */
public interface MessageEvent extends ToJson {

  String getType();
  default String json() {
    return JsonObject.mapFrom(this).encode();
  }
  boolean recipientFilter(User recipient);
}
