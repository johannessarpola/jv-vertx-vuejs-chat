package fi.johannes.bilot.products.commerce.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination {

  @JsonProperty("totalPages")
  private int totalPages;

  @JsonProperty("totalResults")
  private int totalResults;


  public Pagination() {}

  public Pagination(int totalPages, int totalResults) {
    this.totalPages = totalPages;
    this.totalResults = totalResults;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public int getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(int totalResults) {
    this.totalResults = totalResults;
  }
}
