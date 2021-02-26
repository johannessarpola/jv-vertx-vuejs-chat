package fi.johannes.vertx;

import fi.johannes.chat.ChatVerticle;
import fi.johannes.chat.history.ChatHistoryVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainVerticle extends AbstractVerticle {

  protected static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
  private Set<String> args;

  public MainVerticle(String[] args) {
    this.args = new HashSet<>(Arrays.asList(args));
  }

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

        ConfigStoreOptions hvConfStore = new ConfigStoreOptions()
          .setType("file")
          .setFormat("yaml")
          .setConfig(new JsonObject()
            .put("path", "history-configuration.yaml")
          );

        ConfigRetriever retriever = ConfigRetriever.create(vertx,
          new ConfigRetrieverOptions().addStore(hvConfStore));

        retriever.getConfig((ar) -> {
          if(ar.succeeded()) {
            logger.info("conf loaded");

            JsonObject config = ar.result();
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
            logger.info("conf not loaded");
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
    vertx.deployVerticle(new MainVerticle(args));
  }
}
