package fi.bilot.websocket.chat;

import fi.bilot.products.commerce.ProductsApi;
import fi.bilot.products.commerce.ProductsApiImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Johannes on 18.3.2020.
 */
public class ChatVerticle extends AbstractVerticle {
  public static String chatToServer = "chat.to.server";
  public static String chatToClient = "chat.to.client";

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  public Router createRouter() {
    Router router = Router.router(vertx);

    router.route("/test").handler((routingContext) -> {
      routingContext.response().end("Chat works!");
    });

    // Allow events for the designated addresses in/out of the event bus bridge
    BridgeOptions opts = new BridgeOptions()
      .addInboundPermitted(new PermittedOptions().setAddress(chatToServer))
      .addOutboundPermitted(new PermittedOptions().setAddress(chatToClient));

    SockJSHandler sockHandler = SockJSHandler.create(vertx);
    sockHandler.bridge(opts);
    sockHandler.socketHandler( (socket) -> {
      System.out.println("sockHandler");
    });

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
        .allowedHeader("Content-Type"))
      .consumes("application/json")
      .produces("application/json");

    router.route().handler(BodyHandler.create());

    router.route("/room/*").handler(sockHandler);

    EventBus eb = vertx.eventBus();
    // Register to listen for messages coming IN to the server
    eb.consumer("chat.to.server").handler(message -> {
      // Create a timestamp string
      String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
      // Send the message back out to all clients with the timestamp prepended.
      eb.publish("chat.to.client", timestamp + ": " + message.body());
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
