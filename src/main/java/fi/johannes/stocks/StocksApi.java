package fi.johannes.stocks;

import fi.johannes.utils.EventStreamingProducer;

import java.util.function.Consumer;

/**
 *
 */
public interface StocksApi extends EventStreamingProducer {


 void updatesFor(String stockId, Consumer<StockData> dataSink, Runnable onClose);

}

