package fi.johannes.bilot.stocks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 *
 */
public class DummyStockUpdatesThread {
  private String stockId;
  private Consumer<StockData> sink;
  private Runnable onClose;
  private int limit = 10;


  public DummyStockUpdatesThread(String stockId, Consumer<StockData> sink, Runnable onClose) {

    this.stockId = stockId;
    this.sink = sink;
    this.onClose = onClose;

  }

  private StockData generateStockData(double previousPrice, double delta) {
    Double d = delta;
    double startingPrice = previousPrice + d;
    StockData sd = new StockData(stockId, startingPrice, d, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    return sd;
  }

  private void publishFromStream(Stream<StockData>  s) {
    s.limit(this.getLimit()).forEach((sd) -> {
      sink.accept(sd);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }

  public Thread updateThread() {
    return new Thread(() -> {
      Random random = new Random();
      Supplier<Double> delta = () -> random.nextDouble() * (random.nextBoolean() ? -1 : 1) * 10;
      final AtomicReference<Double> previousPrice = new AtomicReference<>(100d );

      Stream<StockData> stockDataStream = Stream.generate(() -> {
        StockData stockData = generateStockData(previousPrice.get(), delta.get());
        previousPrice.accumulateAndGet(stockData.getDelta(), Double::sum);
        return stockData;
      });

      try {
        publishFromStream(stockDataStream);
      } finally {
        System.out.println("Closed");
        onClose.run();
      }
    });
  }


  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}




