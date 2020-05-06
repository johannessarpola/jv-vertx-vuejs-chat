package fi.johannes.chat.history.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Johannes on 6.5.2020.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envelope<T> {

  private Meta meta;
  private Room room;
  private Sender sender;
  private Message<T> message;

}
