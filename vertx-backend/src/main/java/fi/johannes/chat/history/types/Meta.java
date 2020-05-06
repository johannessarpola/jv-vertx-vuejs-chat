package fi.johannes.chat.history.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Johannes on 6.5.2020.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

  private Timestamp timestamp;
  private String clientId;
}
