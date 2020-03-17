package fi.bilot.commerce;

import fi.bilot.commerce.types.Pagination;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 */
public class ProductsApiImpl implements ProductsApi {

  private String base = "https://localhost:9002/rest/v2";
  private String siteId = "electronics";
  private String searchPath = "products/search";
  private Vertx vertx;
  private WebClient webClient;

  public ProductsApiImpl(String base, String siteId, String searchPath, WebClient webClient) {
    this.base = base;
    this.siteId = siteId;
    this.searchPath = searchPath;
    this.webClient = webClient;
  }


  public ProductsApiImpl(Vertx vertx) {

    WebClientOptions options = new WebClientOptions()
      .setUserAgent("My-App/1.2.3");
    options.setKeepAlive(false);
    options.setTrustAll(true); // TODO HOXHOX
    this.webClient = WebClient.create(vertx, options);
    this.vertx = vertx;
  }

  private String formParameters(int page, int pageSize) {
    return String.format("pageSize=%d&currentPage=%d&fields=DEFAULT", pageSize, page);
  }

  private String formAbsoluteurl(String url, String parameters) {
    return String.format("%s?%s", url, parameters);
  }

  private Stream<Buffer> jsonEncodePipeline(Stream<?> stream) {
    return stream
      .map(JsonObject::mapFrom)
      .map(JsonObject::encode)
      .map(this::sseFormat)
      .map(Buffer::buffer);
  }

  private CompletableFuture<Void> getProductChunk(int page, int pageSize, Consumer<Buffer> callback) {
    String url = String.join("/", base, siteId, searchPath);
    String parameters = formParameters(page, pageSize);
    String completeUrl = formAbsoluteurl(url, parameters);
    CompletableFuture<Void> f = new CompletableFuture<Void>();
    Runnable r = () -> {
      webClient.getAbs(completeUrl)
        .putHeader("accept", "application/json")
        .send(ar -> {
          if (ar.succeeded()) {
            HttpResponse<Buffer> response = ar.result();
            JsonArray products = response.bodyAsJsonObject().getJsonArray("products");
            System.out.println("Received response with status code: " + response.statusCode());
            jsonEncodePipeline(products.stream()).forEach(callback::accept);
            f.complete(null);
          } else {
            System.out.println("Reques failed: " + ar.cause().getMessage());
          }
        });
    };
    r.run();
    return f;
  }



  public void streamProducts(WriteStream<Buffer> output) {
    streamProducts(0, 10, output);
  }

  private java.util.stream.Stream<Integer> pageStream(int page, int totalPages) {
    return IntStream.range(page, totalPages).boxed();
  }

  private CompletableFuture<Void> completeAll(List<CompletableFuture<?>> f){
    return CompletableFuture.allOf(f.toArray(new CompletableFuture[f.size()]))
      .thenRun(() -> {
        f.forEach(CompletableFuture::join);
      });
  }
  public void streamProducts(int page, final int pageSize, WriteStream<Buffer> output) {

    String url = String.join("/", base, siteId, searchPath);
    String parameters = formParameters(page, pageSize);
    String completeUrl = formAbsoluteurl(url, parameters);

    webClient.getAbs(completeUrl)
      .putHeader("accept", "application/json")
      .send(ar -> {
        if (ar.succeeded()) {
          Pagination pagination =  ar.result().bodyAsJsonObject().getJsonObject("pagination").mapTo(Pagination.class);
          System.out.println("Received response with status code" + ar.result().statusCode());

          List<CompletableFuture<?>> allF = pageStream(page, pagination.getTotalPages())
            .map((v) -> getProductChunk(v, pageSize, (b) -> output.write(b)))
            .collect(Collectors.toList());


//          CompletableFuture.allOf(allF.toArray(new CompletableFuture[allF.size()]))
//            .thenRun(() -> {
//              allF.stream()
//                .map(CompletableFuture::join)
//                .collect(Collectors.toList());
//              output.write(Buffer.buffer(poisonPill()));
//              output.end();
//            });

          completeAll(allF).thenRun( () -> {
            output.write(Buffer.buffer(poisonPill()));
            output.end();
          });
        }
      });

  }
}

