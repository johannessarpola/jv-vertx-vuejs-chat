package fi.johannes.chat.forwarders;

import fi.johannes.chat.history.ChatHistoryVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ChatHistoryForwarder {
  protected static final Logger logger = LoggerFactory.getLogger(ChatHistoryForwarder.class);

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
    this.eventBus.request(roomHistoryAddress(roomAddress), message.encode(), options, (r) -> logger.info("Received history reply: " +r));
  }
}
