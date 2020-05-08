package fi.johannes.chat.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Johannes on 22.3.2020.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class IdMessage implements MessageEvent {

  private String id;
  private String displayName;
  private String roomId;

  @Override
  public boolean recipientFilter(User recipient) {
    // Don't need to filter by default
    return true;
  }
}
