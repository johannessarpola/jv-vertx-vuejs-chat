package fi.bilot.websocket.chat;

import fi.bilot.websocket.chat.types.AssignedId;
import fi.bilot.websocket.chat.types.MessageEvent;
import fi.bilot.websocket.chat.types.User;
import fi.bilot.websocket.chat.types.UserJoined;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Johannes on 18.3.2020.
 */
public class ChatVerticle extends AbstractVerticle {

  private ChatMediator cm;
  private Map<String, Set<User>> rooms = new ConcurrentHashMap<>();

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  private void welcome(User newUser, Set<User> users) {
    UserJoined m = new UserJoined(newUser.getUserId());
    broadcast(m, users);
  }

  private void broadcast(MessageEvent message, Set<User> recipients) {
    recipients.forEach((connectedUser) -> {
      // Broadcast to other users
      if (message.recipientFilter(connectedUser)) {
        var j = message.json();
        connectedUser.getSocket().writeTextMessage(j);
      }
    });
  }

  private Router initializeRouter() {
    Router router = Router.router(vertx);

    this.cm = new ChatMediator(vertx.eventBus());
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

  private void joinRoom(String roomId, User user) {
    if (rooms.containsKey(roomId)) {
      Set<User> existingUsers = rooms.get(roomId);
      existingUsers.add(user);
      cm.registerUser(roomId, user);
    } else {
      Set<User> s = new HashSet<>();
      s.add(user);
      rooms.put(roomId, s);
      cm.newRoom(roomId, user);
    }
  }

  private Handler<Throwable> onExceptions(User user, String roomId) {
    return (err) -> {
      System.out.println("onExceptions");
      System.out.println(err.getMessage());
      rooms.get(roomId).remove(user);
      user.getSocket().close();
    };
  }

  private Handler<Void> onClose(User user, String roomId) {
    return (err) -> {
      System.out.println("onClose");
      rooms.get(roomId).remove(user);
    };
  }

  private void assignedId(User user) {
    user.getSocket().writeTextMessage(new AssignedId(user.getUserId()).json());
  }


  public Router createRouter() {
    Router router = initializeRouter();

    router.route("/test").handler((routingContext) -> {
      routingContext.response().end("Chat works!");
    });

    router.route("/room/:roomId").handler((routingContext) -> {
      String roomId = routingContext.request().getParam("roomId");
      ServerWebSocket socket = routingContext.request().upgrade();
      User user = new User(socket, roomId, java.util.UUID.randomUUID().toString());

      ReadStream<Buffer> rs = socket;
      WriteStream<Buffer> ws = socket;

      assignedId(user);
      joinRoom(roomId, user);
      welcome(user, rooms.get(roomId));

      socket.exceptionHandler(onExceptions(user, roomId));
      socket.closeHandler(onClose(user, roomId));

      rs.handler((msgb) -> {
        System.out.println("! Readstream");
        System.out.println(msgb.toString());
        cm.onMessage(roomId, msgb);
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
