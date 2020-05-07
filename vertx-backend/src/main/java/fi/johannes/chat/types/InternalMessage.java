package fi.johannes.chat.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Johannes on 20.3.2020.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalMessage implements MessageEvent {

  private String roomId;
  private String senderId;
  private String senderDisplayName;
  private String message;

  @Override
  public String getType() {
    return "chat";
  }

  @Override
  public boolean recipientFilter(User recipient) {
    return !recipient.getUserId().equals(this.senderId);
  }
}
