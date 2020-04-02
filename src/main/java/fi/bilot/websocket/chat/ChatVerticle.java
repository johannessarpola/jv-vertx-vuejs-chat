package fi.bilot.websocket.chat;

import fi.bilot.websocket.chat.types.AssignedId;
import fi.bilot.websocket.chat.types.ChatBusMessage;
import fi.bilot.websocket.chat.types.InternalMessage;
import fi.bilot.websocket.chat.types.InboundMessage;
import fi.bilot.websocket.chat.types.Room;
import fi.bilot.websocket.chat.types.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

/**
 * Johannes on 18.3.2020.
 */
public class ChatVerticle extends AbstractVerticle {

  private ChatRooms rooms;
  private ChatBus bus;

  @Override
  public void init(Vertx vertx, Context context) {
    this.rooms = new ChatRooms();
    this.bus = new ChatBus(vertx.eventBus());
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
      rooms.unregisterUser(roomId, user);
      user.getSocket().close();
    };
  }

  private Handler<Void> onClose(User user, String roomId) {
    return (err) -> {
      System.out.println("onClose");
      rooms.unregisterUser(roomId, user);
    };
  }

  private User newUser(String roomId, ServerWebSocket socket) {
    return new User(socket, roomId, java.util.UUID.randomUUID().toString());
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
      User user = newUser(roomId, socket);

      assignedId(user);
      Room room = rooms.joinRoom(roomId, user);

      // TODO There are closed sockets for some reason
      // TODO There is issue with registering new users as well

      socket.exceptionHandler(onExceptions(user, roomId));
      socket.closeHandler(onClose(user, roomId));

      bus.registerConsumer(room.getAddress(), (msg) -> {
        if(!msg.headers().get("sender").equals(user.getUserId())) {
          socket.writeTextMessage(new JsonObject(msg.body()).encode());
        }
      });

      socket.textMessageHandler( (msg) -> {
        InboundMessage externalMessage = new JsonObject(msg).mapTo(InboundMessage.class);
        InternalMessage internalMessage = new InternalMessage(user.getUserId(), externalMessage.getContents());

        ChatBusMessage<JsonObject> busMessage = new ChatBusMessage<>(room.getAddress(), user.getUserId(), JsonObject.mapFrom(internalMessage));
        bus.sendJson(busMessage, (ar) -> {
          System.out.println("done send");
        });

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
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    Router router = Router.router(vertx);

    vertx.createHttpServer().requestHandler(router).listen(9003, http -> {
      if (http.succeeded()) {
        HttpServer server = http.result();
        ChatVerticle chatVerticle = new ChatVerticle();
        vertx.deployVerticle(chatVerticle, (rs) -> {
          router.mountSubRouter("/chat", chatVerticle.createRouter());
        });
      }
    });
  }
}
