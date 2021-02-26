package fi.johannes.health.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * Johannes on 20.5.2020.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Latency {
  private long duration;
  private long start;

}
