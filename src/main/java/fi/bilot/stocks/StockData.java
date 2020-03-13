package fi.bilot.stocks;

import java.time.LocalDateTime;

/**
 *
 */
public class StockData {

  private String stock;
  private double latestPrice;
  private double delta;
  private String timeStamp;

  public StockData(String stock, double latestPrice, double delta, String timeStamp) {
    this.stock = stock;
    this.latestPrice = latestPrice;
    this.delta = delta;
    this.timeStamp = timeStamp;
  }

  public String getStock() {
    return stock;
  }

  public double getLatestPrice() {
    return latestPrice;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public double getDelta() {
    return delta;
  }

  public void setDelta(double delta) {
    this.delta = delta;
  }

  public void setStock(String stock) {
    this.stock = stock;
  }

  public void setLatestPrice(double latestPrice) {
    this.latestPrice = latestPrice;
  }

  public void setTimeStamp(String timeStamp) {
    this.timeStamp = timeStamp;
  }
}
