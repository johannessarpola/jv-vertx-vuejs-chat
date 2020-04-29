package fi.johannes.bilot.products.commerce;

import fi.johannes.bilot.sse.utils.EventStreamingProducer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;

/**
 *
 */
public interface ProductsApi extends EventStreamingProducer {

  void streamProducts(WriteStream<Buffer> output);
  void streamProducts(int page, int pageSize, WriteStream<Buffer> output);
}
