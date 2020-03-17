package fi.bilot.commerce.types;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;

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
