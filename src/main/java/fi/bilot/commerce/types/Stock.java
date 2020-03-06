package fi.bilot.commerce.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock {
  private int stockLevel;
  private String stockStatus;

  public Stock() {
  }

  public Stock(int stockLevel, String stockStatus) {
    this.stockLevel = stockLevel;
    this.stockStatus = stockStatus;
  }

  public int getStockLevel() {
    return stockLevel;
  }

  public void setStockLevel(int stockLevel) {
    this.stockLevel = stockLevel;
  }

  public String getStockStatus() {
    return stockStatus;
  }

  public void setStockStatus(String stockStatus) {
    this.stockStatus = stockStatus;
  }
}
