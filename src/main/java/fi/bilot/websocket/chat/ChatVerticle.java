package fi.bilot.websocket.chat;

import com.fasterxml.jackson.databind.util.JSONPObject;
import fi.bilot.websocket.chat.types.AssignedId;
import fi.bilot.websocket.chat.types.ChatMessage;
import fi.bilot.websocket.chat.types.MessageEvent;
import fi.bilot.websocket.chat.types.User;
import fi.bilot.websocket.chat.types.UserJoined;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
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

  private Map<String, Set<User>> rooms = new ConcurrentHashMap<>();
  private User serverUser = new User(null, "-1", "Server");
  private int lastHashSent = -1;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  private void welcome(User newUser, Set<User> users) {
    UserJoined m = new UserJoined(newUser.getUserId());
    broadcast(m, users);
  }

  private void broadcast(MessageEvent message, Set<User> recipients) {
    recipients.forEach( (connectedUser) -> {
      // Broadcast to other users
      if(message.recipientFilter(connectedUser)) {
        var j = message.json();
        this.lastHashSent = j.hashCode();
        connectedUser.getSocket().writeTextMessage(j);
      }
    });
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

  public Router createRouter() {
    Router router = initializeRouter();

    router.route("/test").handler((routingContext) -> {
      routingContext.response().end("Chat works!");
    });

    router.route("/room/:roomId").handler( (routingContext) -> {
      String roomId = routingContext.request().getParam("roomId");
      ServerWebSocket socket = routingContext.request().upgrade();
      User user = new User(socket, roomId, java.util.UUID.randomUUID().toString());
      socket.writeTextMessage(new AssignedId(user.getUserId()).json());

      if(rooms.containsKey(roomId)) {
        Set<User> existingUsers = rooms.get(roomId);
        existingUsers.add(user);
      } else {

        Set<User> s = new HashSet<>();
        s.add(user);
        rooms.put(roomId, s);
      }

      welcome(user, rooms.get(roomId));

      socket.exceptionHandler((err) -> {
        System.out.println("Error");
        System.out.println(err.getMessage());
        rooms.get(roomId).remove(user);
        socket.close();
      });

      socket.closeHandler( (close) -> {
        System.out.println("Close");
        rooms.get(roomId).remove(user);
      });

      if(!socket.isClosed()) {
        // TODO Gets into endless loop of text -> broadcast
        socket.textMessageHandler( (msg) -> {
          if(lastHashSent != msg.hashCode()) {
            ChatMessage m = new ChatMessage(user.getUserId(), msg);
            System.out.println("Server received a message: " +msg);
            broadcast(m, rooms.get(roomId));
          }
        });
      }
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
