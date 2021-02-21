package fi.johannes.chat.forwarders;

import fi.johannes.chat.history.ChatHistoryVerticle;
import fi.johannes.chat.types.RoomMessage;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Johannes on 7.5.2020.
 */
public class ChatRoomsForwarder {

  protected static final Logger logger = LoggerFactory.getLogger(ChatRoomsForwarder.class);


  private String newRoomTopic = "rooms.new";
  private String removedRoomsTopic = "rooms.removed";
  private final EventBus eventBus;

  public ChatRoomsForwarder(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public ChatRoomsForwarder(String roomsAddedTopic, String roomsRemovedTopic, EventBus eventBus) {
    this.newRoomTopic = roomsAddedTopic;
    this.removedRoomsTopic = roomsRemovedTopic;
    this.eventBus = eventBus;
  }

  public void newRoom(String roomAddress) {
    logger.info("New room: " + roomAddress);
    this.eventBus.publish(newRoomTopic, new RoomMessage(roomAddress, RoomMessage.RoomAction.New).jsonStr());
  }

  public void removedRoom(String roomAddress) {
    logger.info("Removed room: " + roomAddress);
    this.eventBus.publish(removedRoomsTopic, new RoomMessage(roomAddress, RoomMessage.RoomAction.Removed).jsonStr());
  }
}
