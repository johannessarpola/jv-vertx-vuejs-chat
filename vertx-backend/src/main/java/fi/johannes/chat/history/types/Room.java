package fi.johannes.chat.history.types;

import io.vertx.core.impl.ConcurrentHashSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Johannes on 26.3.2020.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Room {

  private String id;
  private String address;
}
