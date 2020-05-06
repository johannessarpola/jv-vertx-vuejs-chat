package fi.johannes.chat.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Johannes on 2.4.2020.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatBusMessage<T> {
  private String address;
  private String senderId;
  private T message;

}
