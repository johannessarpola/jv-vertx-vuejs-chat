package fi.johannes.chat.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Johannes on 7.5.2020.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomMessage {

  private String roomId;
  private RoomAction action;

  public static enum RoomAction {
    New,
    Removed
  }
}


