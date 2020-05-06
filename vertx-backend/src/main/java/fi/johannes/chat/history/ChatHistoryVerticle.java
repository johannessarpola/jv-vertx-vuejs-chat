package fi.johannes.chat.history;

import fi.johannes.chat.history.types.Envelope;
import fi.johannes.chat.history.types.Message;
import fi.johannes.chat.history.types.Meta;
import fi.johannes.chat.history.types.Room;
import fi.johannes.chat.history.types.Sender;
import fi.johannes.chat.types.ChatBusMessage;
import fi.johannes.chat.types.InternalMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;

import java.sql.Time;
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
  private Map<String, Handler<?>> consumers;
  private Map<String, List<InternalMessage>> messages = new ConcurrentHashMap<>();

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.eventBus = vertx.eventBus();
    this.consumers = new ConcurrentHashMap<>();
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


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);
    eventBus.consumer(roomsAddedTopic).handler((msg) -> {
      System.out.println("roomsAddedTopic");
      System.out.println(msg);
    });
    eventBus.consumer(roomsRemovedTopic).handler((msg) -> {
      System.out.println("roomsRemovedTopic");
      System.out.println(msg);
    });
    eventBus.<String>consumer("room_1").handler((msg) -> {
      InternalMessage m = new JsonObject(msg.body()).mapTo(InternalMessage.class);
      System.out.println("room_1");
      System.out.println(msg);
      System.out.println(m.getSenderId());
      System.out.println(m.getMessage());

      final Envelope<String> envelope = Envelope.<String>builder()
        .message(new Message<>(m.getType(), m.getMessage()))
        .room(new Room("1", "room_1"))
        .meta(new Meta(Timestamp.from(Instant.now()), "vue-chat" ))
        .sender(new Sender(m.getSenderId(), m.getSenderDisplayName()))
        .build();

      KafkaProducerRecord record = KafkaProducerRecord.create("messages", JsonObject.mapFrom(envelope));
      kafkaProducer.send(record, done -> {
        RecordMetadata recordMetadata = done.result();
        System.out.println("Message " + record.value() + " written on topic=" + recordMetadata.getTopic() +
          ", partition=" + recordMetadata.getPartition() +
          ", offset=" + recordMetadata.getOffset());
      });

      if(messages.containsKey("room_1")) {
        messages.put("room_1", new ArrayList<>());
      }
      messages.get("room_1").add(m);

    });

  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    super.stop(stopPromise);
  }

  public Router createRouter() {
    Router router = Router.router(vertx);
    router.route("/test").handler((routingContext) -> {
      routingContext.response().end("Chat works!");
    });

    router.route("/metrics").handler((routingContext) -> { // TODO Change path
      HttpServerResponse httpServerResponse = routingContext.response().putHeader("content-type", "application/json");
      httpServerResponse.end(JsonObject.mapFrom(messages).encodePrettily());
    });

    return router;
  }

  public Map<String, List<InternalMessage>> getMessages() {
    return messages;
  }

  public static void main(String[] args) {
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


  }
}
