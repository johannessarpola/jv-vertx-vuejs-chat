package fi.bilot.commerce.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

  private boolean availableForPickup;
  private double averageRating;
  private String code;
  private boolean configurable;
  private String configuratorType;
  private String description;
  private String manufacturer;
  private boolean multidimensional;
  private String name;
  private Price price;
  private Stock stock;
  private String summary;
  private String url;

  public Product() {
  }

  public Product(boolean availableForPickup,
                 double averageRating,
                 String code,
                 boolean configurable,
                 String configuratorType,
                 String description,
                 String manufacturer,
                 boolean multidimensional,
                 String name, Price price, Stock stock, String summary, String url) {
    this.availableForPickup = availableForPickup;
    this.averageRating = averageRating;
    this.code = code;
    this.configurable = configurable;
    this.configuratorType = configuratorType;
    this.description = description;
    this.manufacturer = manufacturer;
    this.multidimensional = multidimensional;
    this.name = name;
    this.price = price;
    this.stock = stock;
    this.summary = summary;
    this.url = url;
  }

  public boolean isAvailableForPickup() {
    return availableForPickup;
  }

  public void setAvailableForPickup(boolean availableForPickup) {
    this.availableForPickup = availableForPickup;
  }

  public double getAverageRating() {
    return averageRating;
  }

  public void setAverageRating(double averageRating) {
    this.averageRating = averageRating;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public boolean isConfigurable() {
    return configurable;
  }

  public void setConfigurable(boolean configurable) {
    this.configurable = configurable;
  }

  public String getConfiguratorType() {
    return configuratorType;
  }

  public void setConfiguratorType(String configuratorType) {
    this.configuratorType = configuratorType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public boolean isMultidimensional() {
    return multidimensional;
  }

  public void setMultidimensional(boolean multidimensional) {
    this.multidimensional = multidimensional;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Price getPrice() {
    return price;
  }

  public void setPrice(Price price) {
    this.price = price;
  }

  public Stock getStock() {
    return stock;
  }

  public void setStock(Stock stock) {
    this.stock = stock;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
