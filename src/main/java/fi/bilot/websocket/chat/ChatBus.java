package fi.bilot.websocket.chat;

import fi.bilot.websocket.chat.types.ChatBusMessage;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.function.Consumer;

/**
 * Johannes on 2.4.2020.
 */
public class ChatBus {
  private EventBus eventBus;

  public ChatBus(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public void sendJson(ChatBusMessage<JsonObject> message, Handler<AsyncResult<Message<JsonObject>>> replyHandler) {
    DeliveryOptions options = new DeliveryOptions();
    options.addHeader("sender", message.getSender());
    //this.eventBus.request(message.getAddress(), message, replyHandler);
    this.eventBus.send(message.getAddress(), message.getMessage().encode(), options);
  }

  public void registerConsumer(String address, Handler<Message<String>> handler) {
    this.eventBus.localConsumer( address, handler );
  }
}
