package fi.johannes.bilot.products.commerce;

import fi.johannes.bilot.sse.utils.EventStreamingEndpoint;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

/**
 * Johannes on 18.3.2020.
 */
public class ProductVerticle extends AbstractVerticle implements EventStreamingEndpoint {

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  public Router createRouter() {
    Router router = Router.router(vertx);
    ProductsApi productsApi = new ProductsApiImpl(vertx);

    router.route("/test").handler( (routingContext) -> {
      routingContext.response().end("Products works!");
    });

    router.route("/sse").handler( routingContext -> {
      HttpServerResponse response = routingContext.response();
      addStreamingHeaders(response, "http://localhost:4201");
      productsApi.streamProducts(response);

    });

    return router;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    startPromise.complete();

  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    stopPromise.complete();
  }
}
