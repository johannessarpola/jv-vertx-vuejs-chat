package fi.johannes.bilot.stocks;

import fi.johannes.bilot.sse.utils.EventStreamingProducer;

import java.util.function.Consumer;

/**
 *
 */
public interface StocksApi extends EventStreamingProducer {


 void updatesFor(String stockId, Consumer<StockData> dataSink, Runnable onClose);

}

