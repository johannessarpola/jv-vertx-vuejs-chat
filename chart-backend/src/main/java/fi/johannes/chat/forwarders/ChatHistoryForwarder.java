package fi.johannes.chat.forwarders;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 *
 */
public class ChatHistoryForwarder {
  private final EventBus eventBus;

  public ChatHistoryForwarder(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  private String roomHistoryAddress(String roomId) {
    String historySuffix = "history";
    return String.format("%s.%s", roomId, historySuffix);
  }

  public void forwardMessage(String roomAddress, JsonObject message) {
    DeliveryOptions options = new DeliveryOptions();
    this.eventBus.request(roomHistoryAddress(roomAddress), message.encode(), options, (r) -> System.out.println("Received history reply: " +r));
  }
}