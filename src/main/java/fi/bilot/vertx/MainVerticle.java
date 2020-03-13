package fi.bilot.vertx;

import fi.bilot.commerce.ProductsApiImpl;
import fi.bilot.sse.utils.EventStreamingEndpoint;
import fi.bilot.stocks.StockVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle implements EventStreamingEndpoint {

  private Handler<RoutingContext> productsApiRouter() {
    ProductsApiImpl productsApi = new ProductsApiImpl(vertx);
    return (routingContext) -> {
      HttpServerResponse response = routingContext.response();
      addStreamingHeaders(response, "http://localhost:4201");
      productsApi.streamProducts(response);
    };
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);

    router.route("/products/sse").handler(productsApiRouter());

    StockVerticle sv = new StockVerticle();

    vertx.createHttpServer().requestHandler(router).listen(9003, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        vertx.deployVerticle(sv, (result) -> {
          if(result.succeeded()) {
            System.out.println("its ok");
            router.mountSubRouter("/stocks", sv.createStockRouter());
          } else {
            System.out.println("Could not start StockVerticle");
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
