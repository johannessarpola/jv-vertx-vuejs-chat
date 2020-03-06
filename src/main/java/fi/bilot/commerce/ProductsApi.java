package fi.bilot.commerce;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;

/**
 *
 */
public interface ProductsApi {

  void productList(int page, int pageSize, WriteStream<Buffer> output);
}
