package fi.johannes.vertx;

import fi.johannes.chat.history.ChatHistoryVerticle;
import fi.johannes.chat.ChatVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  protected static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);


  @Override
  public void start(Promise<Void> startPromise) {

    logger.info("Starting the main application");
    Router router = Router.router(vertx);
    vertx.createHttpServer().requestHandler(router).listen(9003, http -> {
      if (http.succeeded()) {
        HttpServer server = http.result();
        startPromise.complete();

        logger.info("HTTP server started on port 8888");

        // Chat
        ChatVerticle chat = new ChatVerticle();
        vertx.deployVerticle(chat, (result) -> {
          if (result.succeeded()) {
            logger.info("Started ChatVerticle successfully");
            router.mountSubRouter("/chat", chat.createRouter());
          } else {
            logger.error("Could not start ChatVerticle");
          }
        });

        // Chat history
        ChatHistoryVerticle chatHistory = new ChatHistoryVerticle();
        vertx.deployVerticle(chatHistory, (result) -> {
          if (result.succeeded()) {
            logger.info("Started ChatHistoryVerticle successfully");
            router.mountSubRouter("/history", chatHistory.createRouter());
          } else {
            logger.error("Could not start ChatHistoryVerticle");

          }
        });

      } else {
        logger.error("Failed to start HTTP");
        startPromise.fail(http.cause());
      }
    });

  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
