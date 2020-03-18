package fi.bilot.vertx;

import fi.bilot.products.commerce.ProductVerticle;
import fi.bilot.products.commerce.ProductsApiImpl;
import fi.bilot.sse.utils.EventStreamingEndpoint;
import fi.bilot.stocks.StockVerticle;
import fi.bilot.websocket.chat.ChatVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle implements EventStreamingEndpoint {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);
    vertx.createHttpServer().requestHandler(router).listen(9003, http -> {
      if (http.succeeded()) {
        HttpServer server = http.result();

        startPromise.complete();

        // Stocks
        StockVerticle sv = new StockVerticle();
        vertx.deployVerticle(sv, (result) -> {
          if (result.succeeded()) {
            System.out.println("stocks ok");
            router.mountSubRouter("/stocks", sv.createRouter());
          } else {
            System.out.println("Could not start StockVerticle");
          }
        });

        // Products
        ProductVerticle pv = new ProductVerticle();
        vertx.deployVerticle(pv, (result) -> {
          if (result.succeeded()) {
            System.out.println("products ok");
            router.mountSubRouter("/products", pv.createRouter());
          } else {
            System.out.println("Could not start ProductVerticle");
          }
        });

        // Chat
        ChatVerticle cv = new ChatVerticle();
        vertx.deployVerticle(cv, (result) -> {
          if (result.succeeded()) {
            System.out.println("chat ok");
            router.mountSubRouter("/chat", cv.createRouter());
          } else {
            System.out.println("Could not start ChatVerticle");
          }
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
