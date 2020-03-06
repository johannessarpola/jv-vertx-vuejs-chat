package fi.bilot.vertx;

import fi.bilot.commerce.ProductsApiClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  private HttpServerResponse addStreamingHeaders(HttpServerResponse response) {
    response.setChunked(true);
    response.headers().add("Content-Type", "text/event-stream;charset=UTF-8");
    response.headers().add("Connection", "keep-alive");
    response.headers().add("Cache-Control", "no-cache");
    response.headers().add("Access-Control-Allow-Origin", "http://localhost:4201");
    response.headers().add("Access-Control-Expose-Headers", "*");
    response.headers().add("Access-Control-Allow-Credentials", "true");
    return response;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);

    ProductsApiClient productsApi = new ProductsApiClient(vertx);

    router.route("/sse").handler( routingContext -> {
      HttpServerResponse response = routingContext.response();
      addStreamingHeaders(response);
      JsonObject start = new JsonObject();
      start.put("ok", "ok");
      productsApi.productList(0, 10, response);
    });

    vertx.createHttpServer().requestHandler(router).listen(9003, http -> {
      if (http.succeeded()) {
        startPromise.complete();
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
