package fi.bilot.stocks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 */
public class StocksDummyApiImpl implements StocksApi {

  public Thread getUpdates(String stockId, Consumer<StockData> sink) {
    return new Thread(() -> {

      Random r = new Random();

      Stream<StockData> s = Stream.generate(() -> {
        double delta = r.nextDouble() * 10;
        double startingPrice = 1d;
        StockData sd = new StockData(stockId, startingPrice, delta, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        return sd;
      });

      s.forEach((sd) -> {
        sink.accept(sd);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    });
  }


  @Override
  public void updatesFor(String stockId, Consumer<StockData> dataSink) {
    Thread r = getUpdates(stockId, dataSink); // TODO Close at some point
    r.start();
  }


}
