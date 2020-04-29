package fi.johannes.utils;

import io.vertx.core.http.HttpServerResponse;

/**
 *
 */
public interface EventStreamingEndpoint {

  default HttpServerResponse addStreamingHeaders(HttpServerResponse response, String origin) {
    response.setChunked(true);
    response.headers().add("Content-Type", "text/event-stream;charset=UTF-8");
    response.headers().add("Connection", "keep-alive");
    response.headers().add("Cache-Control", "no-cache");
    response.headers().add("Access-Control-Allow-Origin", origin);
    response.headers().add("Access-Control-Expose-Headers", "*");
    response.headers().add("Access-Control-Allow-Credentials", "true");
    return response;
  }
}
