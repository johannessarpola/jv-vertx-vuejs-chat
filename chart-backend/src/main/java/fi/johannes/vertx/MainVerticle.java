package fi.johannes.vertx;

import fi.johannes.chat.history.ChatHistoryVerticle;
import fi.johannes.chat.ChatVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {

    Router router = Router.router(vertx);
    vertx.createHttpServer().requestHandler(router).listen(9003, http -> {
      if (http.succeeded()) {
        HttpServer server = http.result();
        startPromise.complete();

        // Chat
        ChatVerticle chat = new ChatVerticle();
        vertx.deployVerticle(chat, (result) -> {
          if (result.succeeded()) {
            System.out.println("chat ok");
            router.mountSubRouter("/chat", chat.createRouter());
          } else {
            System.out.println("Could not start ChatVerticle");
          }
        });

        // Chat history
        ChatHistoryVerticle chatHistory = new ChatHistoryVerticle();
        vertx.deployVerticle(chatHistory, (rs) -> {
          router.mountSubRouter("/history", chatHistory.createRouter());
        });

        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });

  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
