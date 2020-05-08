package fi.johannes.chat;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 *
 */
public class ChatHistory {
  private EventBus eventBus;
  private String historySuffix = "history";

  public ChatHistory(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  private String address(String roomId) {
    return String.format("%s.%s", roomId, historySuffix);
  }

  public void messageSent(String roomAddress, JsonObject message) {
    DeliveryOptions options = new DeliveryOptions();
    this.eventBus.request(address(roomAddress), message.encode(), options, (r) -> System.out.println("Received history reply: " +r));
  }
}
