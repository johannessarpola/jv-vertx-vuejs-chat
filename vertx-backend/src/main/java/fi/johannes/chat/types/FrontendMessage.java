package fi.johannes.chat.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FrontendMessage {

  private String date;
  private String contents;

  public FrontendMessage(String date, String contents) {
    this.date = date;
    this.contents = contents;
  }

  public FrontendMessage() {
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getContents() {
    return contents;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }
}
