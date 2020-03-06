package fi.bilot.commerce.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination {

  private int totalPages;
  private int totalResults;

  public Pagination(int totalPages, int totalResults) {
    this.totalPages = totalPages;
    this.totalResults = totalResults;
  }

  public Pagination() {
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
