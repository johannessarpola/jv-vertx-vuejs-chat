package fi.johannes.stocks;

import java.util.function.Consumer;

/**
 *
 */
public class StocksDummyApiImpl implements StocksApi {

  @Override
  public void updatesFor(String stockId, Consumer<StockData> dataSink, Runnable onClose) {
    DummyStockUpdatesThread dummyUpdates = new DummyStockUpdatesThread(stockId, dataSink, onClose);
    Thread r = dummyUpdates.updateThread();
    dummyUpdates.setLimit(100);
    r.start();
  }


}
