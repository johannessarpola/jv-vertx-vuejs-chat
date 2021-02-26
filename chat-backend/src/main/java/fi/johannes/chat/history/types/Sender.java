package fi.johannes.chat.history.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Johannes on 6.5.2020.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Sender {

  private String id;
  private String displayName;

}
