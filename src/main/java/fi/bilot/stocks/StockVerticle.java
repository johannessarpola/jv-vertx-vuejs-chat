package fi.bilot.stocks;

import com.fasterxml.jackson.databind.util.JSONPObject;
import fi.bilot.sse.utils.EventStreamingEndpoint;
import fi.bilot.sse.utils.EventStreamingProducer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.stream.Stream;

/**
 *
 */
public class StockVerticle extends AbstractVerticle implements EventStreamingEndpoint, EventStreamingProducer {

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }


  public Router createStockRouter() {
    Router router = Router.router(vertx);

    StocksApi stocksApi = new StocksDummyApiImpl();

    router.route("/test").handler( (routingContext) -> {
      routingContext.response().end("Stocks works!");
    });

    router.route("/sse").handler( routingContext -> {
      HttpServerResponse response = routingContext.response();
      addStreamingHeaders(response, "http://localhost:3000");
      JsonObject start = new JsonObject();
      start.put("ok", "ok");

      stocksApi.updatesFor("G00GL", (sd) -> response.write(sseFormat(JsonObject.mapFrom(sd).encode()) )); // TODO Quit condition

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
