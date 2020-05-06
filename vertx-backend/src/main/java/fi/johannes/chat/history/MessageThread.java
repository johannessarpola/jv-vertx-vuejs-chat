package fi.johannes.chat.history;

import fi.johannes.chat.types.ChatBusMessage;
import fi.johannes.chat.types.InternalMessage;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Johannes on 1.5.2020.
 */
public class MessageThread {

  private final int limit;
  private final EventBus bus;
  private final Runnable onClose;

  public MessageThread(
    EventBus bus,
    Runnable onClose) {

    this.bus = bus;
    this.onClose = onClose;
    this.limit = 100;
  }

  private void publishFromStream(Stream<ChatBusMessage<JsonObject>> s) {
    s.limit(this.limit).forEach((sd) -> {
      sendJson(sd, (r) -> {});
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }

  private void sendJson(ChatBusMessage<JsonObject> message, Handler<AsyncResult<Message<JsonObject>>> replyHandler) {
    DeliveryOptions options = new DeliveryOptions();
    options.addHeader("sender", message.getSenderId());
    this.bus.publish(message.getAddress(), message.getMessage().encode(), options);
  }

  public Thread messageThread() {
    return new Thread(() -> {

      Stream<ChatBusMessage<JsonObject>> generatedStream = Stream.generate(() -> new ChatBusMessage<>(
        "room_1",
        "senderId",
        JsonObject.mapFrom(new InternalMessage(UUID.randomUUID().toString(), "display", "hello"))));
      try {
        publishFromStream(generatedStream);
      } finally {
        System.out.println("Closed");
        onClose.run();
      }
    });
  }
}