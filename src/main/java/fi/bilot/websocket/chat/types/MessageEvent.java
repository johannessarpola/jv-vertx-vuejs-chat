package fi.bilot.websocket.chat.types;

import io.vertx.core.json.JsonObject;

import java.util.Set;

/**
 * Johannes on 22.3.2020.
 */
public interface MessageEvent {

  String getType();
  default String json() {
    return JsonObject.mapFrom(this).encode();
  }
  boolean recipientFilter(User recipient);
}
