package fi.bilot.commerce.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {
  private String currencyIso;
  private String formattedValue;
  private String priceType;
  private double value;

  public Price() {
  }

  public Price(String currencyIso, String formattedValue, String priceType, double value) {
    this.currencyIso = currencyIso;
    this.formattedValue = formattedValue;
    this.priceType = priceType;
    this.value = value;
  }

  public String getCurrencyIso() {
    return currencyIso;
  }

  public void setCurrencyIso(String currencyIso) {
    this.currencyIso = currencyIso;
  }

  public String getFormattedValue() {
    return formattedValue;
  }

  public void setFormattedValue(String formattedValue) {
    this.formattedValue = formattedValue;
  }

  public String getPriceType() {
    return priceType;
  }

  public void setPriceType(String priceType) {
    this.priceType = priceType;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }
}
