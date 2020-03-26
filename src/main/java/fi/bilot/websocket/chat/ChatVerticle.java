package fi.bilot.websocket.chat;

import fi.bilot.vertx.MainVerticle;
import fi.bilot.websocket.chat.types.AssignedId;
import fi.bilot.websocket.chat.types.ChatMessage;
import fi.bilot.websocket.chat.types.InboundMessage;
import fi.bilot.websocket.chat.types.User;
import fi.bilot.websocket.chat.types.UserJoined;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.Set;

/**
 * Johannes on 18.3.2020.
 */
public class ChatVerticle extends AbstractVerticle {

  private ChatMediator cm;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
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

  private Handler<Throwable> onExceptions(User user, String roomId) {
    return (err) -> {
      System.out.println("onExceptions");
      System.out.println(err.getMessage());
      cm.unregisterUser(roomId, user);
      user.getSocket().close();
    };
  }

  private Handler<Void> onClose(User user, String roomId) {
    return (err) -> {

      System.out.println("onClose");
      cm.unregisterUser(roomId, user);
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
      System.out.println("Connectino opened to room " + roomId);
      ServerWebSocket socket = routingContext.request().upgrade();
      User user = new User(socket, roomId, java.util.UUID.randomUUID().toString());

      ReadStream<Buffer> rs = socket;
      WriteStream<Buffer> ws = socket;

      assignedId(user);
      cm.joinRoom(roomId, user);
      cm.send(roomId, new UserJoined(user.getUserId()));

      socket.exceptionHandler(onExceptions(user, roomId));
      socket.closeHandler(onClose(user, roomId));

      rs.handler((msg) -> {
        InboundMessage chatMessage = new JsonObject(msg).mapTo(InboundMessage.class);
        ChatMessage newMessage = new ChatMessage(user.getUserId(), chatMessage.getContents());
        System.out.println("! Readstream");
        System.out.println(msg.toString());
        cm.send(roomId, newMessage);
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
