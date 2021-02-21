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
import io.vertx.kafka.client.producer.KafkaProducerRecord;

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
  private final String kafkaTopic = "messages";
  private final String roomHistorySuffix = "history";
  private final String roomsAddedTopic = "rooms.new";
  private final String roomsRemovedTopic = "rooms.removed";
  private KafkaProducer<String, JsonObject> kafkaProducer;
  private Map<String, MessageConsumer<?>> consumers = new ConcurrentHashMap<>();

  private KafkaProducer<String, JsonObject> initializeKafkaProducer() {
    Map<String, String> kafkaProducerConf = new HashMap<>();
    // TODO Externalize
    kafkaProducerConf.put("bootstrap.servers", "localhost:19092");
    kafkaProducerConf.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    kafkaProducerConf.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    kafkaProducerConf.put("value.deserializer", "io.vertx.kafka.client.serialization.JsonObjectDeserializer");
    kafkaProducerConf.put("value.serializer", "io.vertx.kafka.client.serialization.JsonObjectSerializer");
    kafkaProducerConf.put("acks", "1");
    return KafkaProducer.create(vertx, kafkaProducerConf);
  }

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.eventBus = vertx.eventBus();
    this.kafkaProducer = initializeKafkaProducer();
  }

  private Envelope<String> newEnvelope(InternalMessage m, String roomAddress) {
    final Envelope<String> envelope = Envelope.<String>builder()
      .message(new Message<>(m.getType(), m.getMessage()))
      .room(new Room(m.getRoomId(), roomAddress))
      .meta(new Meta(Timestamp.from(Instant.now()), "vue-chat"))
      .sender(new Sender(m.getSenderId(), m.getSenderDisplayName()))
      .build();
    return envelope;
  }

  private MessageConsumer<String> newConsumer(String roomAddress) {
    return eventBus.<String>consumer(String.format("%s.%s", roomAddress, roomHistorySuffix)).handler((msg) -> {
      System.out.println("History consumer: " + roomAddress);
      System.out.println("Received message: " + msg.body());
      InternalMessage m = new JsonObject(msg.body()).mapTo(InternalMessage.class);
      Envelope<String> envelope = newEnvelope(m, roomAddress);

      KafkaProducerRecord record = KafkaProducerRecord.create(this.kafkaTopic, envelope.json());
      kafkaProducer.send(record);

      msg.reply(Boolean.TRUE);
    });
  }

  private void registerNewRoom(String roomAddress) {
    System.out.println("Registered room: " + roomAddress);
    MessageConsumer<String> consumer = newConsumer(roomAddress);
    if (!this.consumers.containsKey(roomAddress) || this.consumers.getOrDefault(roomAddress, null) == null) {
      this.consumers.put(roomAddress, consumer);
    }
  }

  private void unregisterRoom(String roomAddress) {
    System.out.println("Unregistered room: " + roomAddress);
    if (this.consumers.containsValue(roomAddress)) {
      this.consumers.get(roomAddress).unregister();
      this.consumers.remove(roomAddress);
    }
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);

    eventBus.<String>consumer(roomsAddedTopic).handler((msg) -> {
      System.out.println("Rooms added topic: " + msg.body());
      RoomMessage newRoom = new JsonObject(msg.body()).mapTo(RoomMessage.class);
      registerNewRoom(newRoom.getRoomAddress());
    });
    eventBus.<String>consumer(roomsRemovedTopic).handler((msg) -> {
      System.out.println("Rooms removed topic: " + msg.body());
      RoomMessage removedRoom = new JsonObject(msg.body()).mapTo(RoomMessage.class);
      unregisterRoom(removedRoom.getRoomAddress());
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

    return router;
  }

}
