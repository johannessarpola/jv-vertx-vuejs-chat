package fi.johannes.chat.history;

import fi.johannes.chat.ChatVerticle;
import fi.johannes.chat.history.types.Envelope;
import fi.johannes.chat.history.types.Message;
import fi.johannes.chat.history.types.Meta;
import fi.johannes.chat.history.types.Room;
import fi.johannes.chat.history.types.Sender;
import fi.johannes.chat.types.InternalMessage;
import fi.johannes.chat.types.RoomMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  protected static final Logger logger = LoggerFactory.getLogger(ChatHistoryVerticle.class);

  private int retryAttempts = 5;

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

    kafkaProducerConf.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
    kafkaProducerConf.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    kafkaProducerConf.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.vertx.kafka.client.serialization.JsonObjectSerializer");
    kafkaProducerConf.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, "10000");
    kafkaProducerConf.put(ProducerConfig.ACKS_CONFIG, "1");
    return KafkaProducer.create(vertx, kafkaProducerConf);
  }

  @Override
  public void start() throws Exception {
    try {
      this.eventBus = vertx.eventBus();
      this.kafkaProducer = initializeKafkaProducer();
    } catch (Exception e) {
      logger.error("Could not initialize KafkaProducer");
    }
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
      logger.info("History consumer: " + roomAddress);
      logger.info("Received message: " + msg.body());
      InternalMessage m = new JsonObject(msg.body()).mapTo(InternalMessage.class);
      Envelope<String> envelope = newEnvelope(m, roomAddress);

      KafkaProducerRecord record = KafkaProducerRecord.create(this.kafkaTopic, envelope.json());
      kafkaProducer.send(record);

      msg.reply(Boolean.TRUE);
    });
  }

  private void registerNewRoom(String roomAddress) {
    logger.info("Registered room: " + roomAddress);
    MessageConsumer<String> consumer = newConsumer(roomAddress);
    if (!this.consumers.containsKey(roomAddress) || this.consumers.getOrDefault(roomAddress, null) == null) {
      this.consumers.put(roomAddress, consumer);
    }
  }

  private void unregisterRoom(String roomAddress) {
    logger.info("Unregistered room: " + roomAddress);
    if (this.consumers.containsValue(roomAddress)) {
      this.consumers.get(roomAddress).unregister();
      this.consumers.remove(roomAddress);
    }
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);

    eventBus.<String>consumer(roomsAddedTopic).handler((msg) -> {
      logger.info("Rooms added topic: " + msg.body());
      RoomMessage newRoom = new JsonObject(msg.body()).mapTo(RoomMessage.class);
      registerNewRoom(newRoom.getRoomAddress());
    });
    eventBus.<String>consumer(roomsRemovedTopic).handler((msg) -> {
      logger.info("Rooms removed topic: " + msg.body());
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
