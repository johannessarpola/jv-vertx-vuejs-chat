package fi.bilot.stocks;

import fi.bilot.sse.utils.EventStreamingProducer;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 */
public interface StocksApi extends EventStreamingProducer {


 void updatesFor(String stockId, Consumer<StockData> dataSink, Runnable onClose);

}

