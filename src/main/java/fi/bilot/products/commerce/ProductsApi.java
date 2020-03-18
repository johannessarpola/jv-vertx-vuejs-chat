package fi.bilot.products.commerce;

import fi.bilot.sse.utils.EventStreamingProducer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;

/**
 *
 */
public interface ProductsApi extends EventStreamingProducer {

  void streamProducts(WriteStream<Buffer> output);
  void streamProducts(int page, int pageSize, WriteStream<Buffer> output);
}
