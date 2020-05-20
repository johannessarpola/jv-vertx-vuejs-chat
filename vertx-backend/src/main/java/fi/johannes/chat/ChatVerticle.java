package fi.johannes.chat;

import fi.johannes.chat.animals.AnimalsInstance;
import fi.johannes.chat.animals.types.Animal;
import fi.johannes.chat.forwarders.ChatHistoryForwarder;
import fi.johannes.chat.types.AssignedId;
import fi.johannes.chat.types.FrontendMessage;
import fi.johannes.chat.types.InternalMessage;
import fi.johannes.chat.types.MessageEvent;
import fi.johannes.chat.types.Room;
import fi.johannes.chat.types.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Johannes on 18.3.2020.
 */
public class ChatVerticle extends AbstractVerticle {

  private AnimalsInstance animals;

  private ChatRooms rooms;
  private ChatBus chatBus;
  private ChatHistoryForwarder history;

  @Override
  public void init(Vertx vertx, Context context) {
    this.rooms = new ChatRooms(vertx.eventBus());
    this.chatBus = new ChatBus(vertx.eventBus());
    this.history = new ChatHistoryForwarder(vertx.eventBus());
    this.animals = AnimalsInstance.getInstance();
    super.init(vertx, context);
  }

  private Router initializeRouter() {
    Router router = Router.router(vertx);

    router.route().handler(
      CorsHandler.create("http://localhost:8080")
        .allowedMethod(io.vertx.core.http.HttpMethod.GET)
        .allowedMethod(io.vertx.core.http.HttpMethod.POST)
        .allowedMethod(io.vertx.core.http.HttpMethod.PUT)
        .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
        .allowCredentials(true)
        .allowedHeader("Access-Control-Allow-Method")
        .allowedHeader("Access-Control-Allow-Origin")
        .allowedHeader("Access-Control-Allow-Credentials")
        .allowedHeader("Content-Type"));

    router.route().handler(BodyHandler.create());

    return router;
  }

  private void closeOrException(User user, String roomId) {
    user.getConsumer().unregister();
    rooms.removeUser(roomId, user);
    user.getSocket().close();
  }

  private Handler<Throwable> socketOnExceptions(User user, String roomId) {
    return (err) -> {
      System.out.println("onExceptions");
      System.out.println(err.getMessage());
      closeOrException(user, roomId);
    };
  }

  private Handler<Void> socketOnClose(User user, String roomId) {
    return (err) -> {
      System.out.println("onClose");
      closeOrException(user, roomId);
    };
  }

  private void sendAssignedId(User user, String roomAddress) {
    MessageEvent me = new AssignedId(user.getUserId(), user.getDisplayName(), roomAddress);
    user.getSocket().writeTextMessage(me.jsonStr());
    // TODO Enable at some point
    // bus.publishJson(user.getUserId(), roomAddress, me.json());
  }

  private Optional<String> generateDisplayName(String roomId) {
    Predicate<Animal> onlyUniques = (a) -> !this.rooms.getUserDisplayNames().contains(a.getName());
    Optional<String> displayName = Optional.ofNullable(this.animals)
      .map((as) -> as.getAnimal(onlyUniques))
      .map(Animal::getName);
    return displayName;
  }

  public Router createRouter() {
    Router router = initializeRouter();

    router.route("/healthcheck").handler((routingContext) -> {
      routingContext.response().end("Chat works!");
    });

    router.route("/room/:roomId").handler((routingContext) -> {
      String roomId = routingContext.request().getParam("roomId");
      ServerWebSocket socket = routingContext.request().upgrade();
      Room room = new Room(roomId);
      String userId = UUID.randomUUID().toString();

      String displayName = generateDisplayName(roomId).orElse(userId);

      // Eventbus consumer
      MessageConsumer<String> consumer = chatBus.registerConsumer(room.getAddress(), (msg) -> {
        System.out.println("Receiver: "+userId+"_"+displayName);
        System.out.println("Consuming message: " +msg.body().toString());
        if (!msg.headers().get("sender").equals(userId) && !socket.isClosed()) {
          socket.writeTextMessage(new JsonObject(msg.body()).encode());
        }
      });

      User user = new User(socket, room.getId(), userId, consumer, displayName);
      sendAssignedId(user, room.getAddress());
      rooms.joinRoom(room, user);

      socket.exceptionHandler(socketOnExceptions(user, roomId));
      socket.closeHandler(socketOnClose(user, roomId));

      // TODO Seems to be issue with recreating this when same window is used with multiple windows
      // Might want to investigate how the socket is handled in the backend when it is recreated from the front end
      socket.textMessageHandler( (msg) -> {
        FrontendMessage fm = new JsonObject(msg).mapTo(FrontendMessage.class);
        InternalMessage im = new InternalMessage(roomId, user.getUserId(), user.getDisplayName(), fm.getContents());

        JsonObject json = JsonObject.mapFrom(im);
        chatBus.publishJson(user.getUserId(), room.getAddress(), json);
        history.forwardMessage(room.getAddress(), json);
      });
    });

    return router;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    super.stop(stopPromise);
  }

}
