package fi.johannes.chat;

import fi.johannes.chat.animals.AnimalsInstance;
import fi.johannes.chat.animals.types.Animal;
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
  private ChatBus bus;
  private ChatHistory history;

  @Override
  public void init(Vertx vertx, Context context) {
    this.rooms = new ChatRooms(vertx.eventBus());
    this.bus = new ChatBus(vertx.eventBus());
    this.history = new ChatHistory(vertx.eventBus());
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

  private Handler<Throwable> onExceptions(User user, String roomId) {
    return (err) -> {
      System.out.println("onExceptions");
      System.out.println(err.getMessage());
      user.getConsumer().unregister();
      rooms.removeUser(roomId, user);
      user.getSocket().close();
    };
  }

  private Handler<Void> onClose(User user, String roomId) {
    return (err) -> {
      // TODO Use broker to remove room
      System.out.println("onClose");
      user.getConsumer().unregister();
      rooms.removeUser(roomId, user);
    };
  }

  private void assignedId(User user, String roomAddress) {
    MessageEvent me = new AssignedId(user.getUserId(), user.getDisplayName(), roomAddress);
    user.getSocket().writeTextMessage(me.jsonStr());
    // TODO Enable at some point
    // bus.publishJson(user.getUserId(), roomAddress, me.json());
  }

  private Optional<String> getAnimal(String roomId) {
    Predicate<Animal> onlyUniques = (a) -> !this.rooms.getUserDisplayNames().contains(a.getName());
    Optional<String> displayName = Optional.ofNullable(this.animals)
      .map((as) -> as.getAnimal(onlyUniques))
      .map(Animal::getName);
    return displayName;
  }

  public Router createRouter() {
    Router router = initializeRouter();

    router.route("/healthcheck").handler((routingContext) -> {
      // TODO Output data
      routingContext.response().end("Chat works!");
    });

    router.route("/room/:roomId").handler((routingContext) -> {
      String roomId = routingContext.request().getParam("roomId");
      ServerWebSocket socket = routingContext.request().upgrade();
      Room room = new Room(roomId);
      String userId = UUID.randomUUID().toString();

      String displayName = getAnimal(roomId).orElse(userId);

      // Eventbus consumer
      MessageConsumer<String> consumer = bus.registerConsumer(room.getAddress(), (msg) -> {
        System.out.println("Receiver: "+userId+"_"+displayName);
        System.out.println("Consuming message: " +msg.body().toString());
        if (!msg.headers().get("sender").equals(userId) && !socket.isClosed()) {
          socket.writeTextMessage(new JsonObject(msg.body()).encode());
        }
      });


      User user = new User(socket, room.getId(), userId, consumer, displayName);
      assignedId(user, room.getAddress());
      rooms.joinRoom(room, user);

      socket.exceptionHandler(onExceptions(user, roomId));
      socket.closeHandler(onClose(user, roomId));

      // TODO Seems to be issue with recreating this when same window is used with multiple windows
      // Might want to investigate how the socket is handled in the backend when it is recreated from the front end
      socket.textMessageHandler( (msg) -> {
        FrontendMessage fm = new JsonObject(msg).mapTo(FrontendMessage.class);
        InternalMessage im = new InternalMessage(roomId, user.getUserId(), user.getDisplayName(), fm.getContents());

        JsonObject json = JsonObject.mapFrom(im);
        bus.publishJson(user.getUserId(), room.getAddress(), json);
        history.messageSent(room.getAddress(), json);
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