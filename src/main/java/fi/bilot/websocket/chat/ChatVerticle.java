package fi.bilot.websocket.chat;

import fi.bilot.websocket.chat.types.Message;
import fi.bilot.websocket.chat.types.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Johannes on 18.3.2020.
 */
public class ChatVerticle extends AbstractVerticle {

  private Map<String, Set<User>> rooms = new ConcurrentHashMap<>();

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  public Router createRouter() {
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

    router.route("/test").handler((routingContext) -> {
      routingContext.response().end("Chat works!");
    });

    router.route("/room/:roomId").handler( (routingContext) -> {
      String roomId = routingContext.request().getParam("roomId");
      ServerWebSocket socket = routingContext.request().upgrade();
      User user = new User(socket, roomId, java.util.UUID.randomUUID().toString());

      if(rooms.containsKey(roomId)) {
        rooms.get(roomId).add(user);
      } else {
        Set<User> s = new HashSet<>();
        s.add(user);
        rooms.put(roomId, s);
      }

      socket.exceptionHandler((err) -> {
        System.out.println("Error");
        System.out.println(err.getMessage());
        rooms.get(roomId).remove(socket);
        socket.close();
      });

      socket.closeHandler( (close) -> {
        System.out.println("Close");
        rooms.get(roomId).remove(socket);
      });

      // TODO Custom event type for welcome

      if(!socket.isClosed()) {
        socket.writeTextMessage(new Message(user, "Assigned id").json());
        socket.textMessageHandler( (msg) -> {
          Message m = new Message(user, msg);
          System.out.println("Server received a message: " +msg);
          rooms.get(roomId).forEach( (connectedUser) -> {
            // Broadcast to other users
            if(connectedUser != user) {
              connectedUser.getSocket().writeTextMessage(m.json());
            }
          });
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
