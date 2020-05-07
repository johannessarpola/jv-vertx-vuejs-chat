package fi.johannes.chat.history;

import fi.johannes.chat.history.types.Envelope;
import fi.johannes.chat.history.types.Message;
import fi.johannes.chat.history.types.Meta;
import fi.johannes.chat.history.types.Room;
import fi.johannes.chat.history.types.Sender;
import fi.johannes.chat.types.InternalMessage;
import fi.johannes.chat.types.RoomMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.kafka.client.producer.KafkaProducer;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Johannes on 1.5.2020.
 */
public class ChatHistoryVerticle extends AbstractVerticle {

  private EventBus eventBus;
  private String roomsAddedTopic = "rooms.new";
  private String roomsRemovedTopic = "rooms.removed";
  private Map<String, String> kafkaProducerConf;
  private KafkaProducer<String, JsonObject> kafkaProducer;
  private Map<String, List<InternalMessage>> messages = new ConcurrentHashMap<>();
  private Map<String, MessageConsumer<?>> consumers = new ConcurrentHashMap<>();

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.eventBus = vertx.eventBus();
    Map<String, Handler<?>> consumers = new ConcurrentHashMap<>();
    //    this.messages = new ConcurrentHashMap<>();

    // TODO Externalize
    this.kafkaProducerConf = new HashMap<>();
    kafkaProducerConf.put("bootstrap.servers", "localhost:19092");
    kafkaProducerConf.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    kafkaProducerConf.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    kafkaProducerConf.put("value.deserializer", "io.vertx.kafka.client.serialization.JsonObjectDeserializer");
    kafkaProducerConf.put("value.serializer", "io.vertx.kafka.client.serialization.JsonObjectSerializer");
    kafkaProducerConf.put("acks", "1");

    this.kafkaProducer = KafkaProducer.create(vertx, kafkaProducerConf);

  }

  private Envelope<String> newEnvelope(InternalMessage m, String roomId) {
    final Envelope<String> envelope = Envelope.<String>builder()
      .message(new Message<>(m.getType(), m.getMessage()))
      .room(new Room("1", "room_1"))
      .meta(new Meta(Timestamp.from(Instant.now()), "vue-chat"))
      .sender(new Sender(m.getSenderId(), m.getSenderDisplayName()))
      .build();
    return envelope;
  }

  private MessageConsumer<String> newConsumer(String roomId) {
    return eventBus.<String>consumer(roomId).handler((msg) -> {
      System.out.println(roomId);
      System.out.println(msg);
      InternalMessage m = new JsonObject(msg.body()).mapTo(InternalMessage.class);
      Envelope<String> envelope = newEnvelope(m, roomId);

      // TODO Just for debugging
      if (!messages.containsKey(roomId)) {
        messages.put(roomId, new ArrayList<>());
      }
      messages.get(roomId).add(m);

    });
  }

  private void registerNewRoom(String roomId) {
    MessageConsumer<String> consumer = newConsumer(roomId);
    this.consumers.put(roomId, consumer);
  }

  private void unregisterRoom(String roomId) {
    if (this.consumers.containsKey(roomId)) {
      this.consumers.get(roomId).unregister();
      this.consumers.remove(roomId);
    }
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);
    eventBus.<String>consumer(roomsAddedTopic).handler((msg) -> {
      RoomMessage newRoom = new JsonObject(msg.body()).mapTo(RoomMessage.class);
      registerNewRoom(newRoom.getRoomId());
    });
    eventBus.<String>consumer(roomsRemovedTopic).handler((msg) -> {
      RoomMessage newRoom = new JsonObject(msg.body()).mapTo(RoomMessage.class);
      unregisterRoom(newRoom.getRoomId());
    });
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    super.stop(stopPromise);
  }

  public Router createRouter() {
    Router router = Router.router(vertx);
    router.route("/healthcheck").handler((routingContext) -> {
      // TODO Output data
      routingContext.response().end("Chat works!");
    });

/*    router.route("/metrics").handler((routingContext) -> { // TODO Change path
      HttpServerResponse httpServerResponse = routingContext.response().putHeader("content-type", "application/json");
      httpServerResponse.end(JsonObject.mapFrom(messages).encodePrettily());
    });*/

    return router;
  }

  public Map<String, List<InternalMessage>> getMessages() {
    return messages;
  }

/*  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    Router router = Router.router(vertx);

    ChatHistoryVerticle historyVerticle = new ChatHistoryVerticle();
    vertx.deployVerticle(historyVerticle, (rs) -> {
      router.mountSubRouter("/history", historyVerticle.createRouter());
    });

    // TODO Remove
    historyVerticle.getMessages().put("room_1", new ArrayList<>());
    new MessageThread(vertx.eventBus(), () -> System.out.println("Closed")).messageThread().start();

    vertx.createHttpServer().requestHandler(router).listen(9003, http -> {
      if (http.succeeded()) {
        HttpServer server = http.result();
        System.out.println("Server running");
      }
    });


  }*/
}
