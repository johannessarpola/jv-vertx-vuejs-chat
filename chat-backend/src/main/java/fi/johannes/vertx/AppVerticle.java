package fi.johannes.vertx;

import fi.johannes.chat.ChatVerticle;
import fi.johannes.chat.history.ChatHistoryVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Johannes on 6.3.2021.
 */
public class AppVerticle extends AbstractVerticle {
  protected static final Logger logger = LoggerFactory.getLogger(AppVerticle.class);

  public AppVerticle() {
  }

  private void deployChat(Router router, Vertx vertx) {
    ChatVerticle chat = new ChatVerticle();
    vertx.deployVerticle(chat, (result) -> {
      if (result.succeeded()) {
        logger.info("Started ChatVerticle successfully");
        router.mountSubRouter("/chat", chat.createRouter());
      } else {
        logger.error("Could not start ChatVerticle");
      }
    });
  }

  private void deployChatHistory(Router router, Vertx vertx) {
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
  }


  @Override
  public void start(Promise<Void> startPromise) {
    logger.info("Starting the main application");

    ConfigRetriever retriever = ConfigRetriever.create(vertx);

    retriever.getConfig(ar -> {
      logger.info("Loaded configuration");


      if (ar.failed()) {
        startPromise.fail(ar.cause());
      } else {
        JsonObject config = ar.result();
        String env = config.getString("ENV");

        Integer port = config.getInteger("HTTP_PORT");
        Router router = Router.router(vertx);
        vertx.createHttpServer().requestHandler(router).listen(port, http -> {
          logger.info("HTTP server started on port "+port);
          if (http.succeeded()) {
            HttpServer server = http.result();

            // Handles room chats with sockets
            deployChat(router, vertx);
            Boolean useKafka = config.getBoolean("USE_KAFKA");
            if (useKafka) deployChatHistory(router, vertx);

            startPromise.complete();
          } else {
            logger.error("Failed to start HTTP");
            startPromise.fail(http.cause());
          }
        });

      }
    });
  }
}

