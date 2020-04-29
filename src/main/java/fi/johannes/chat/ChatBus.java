package fi.johannes.chat;

import fi.johannes.chat.types.ChatBusMessage;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

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
    options.addHeader("sender", message.getSenderId());
    this.eventBus.publish(message.getAddress(), message.getMessage().encode(), options);
  }

  public MessageConsumer<String> registerConsumer(String address, Handler<Message<String>> handler) {
    MessageConsumer<String> stringMessageConsumer = this.eventBus.consumer(address, handler);
    return stringMessageConsumer;
  }

}
