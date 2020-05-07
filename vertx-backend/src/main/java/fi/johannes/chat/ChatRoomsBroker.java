package fi.johannes.chat;

import fi.johannes.chat.types.RoomMessage;
import io.vertx.core.eventbus.EventBus;

/**
 * Johannes on 7.5.2020.
 */
public class ChatRoomsBroker {

  private String newRoomTopic = "rooms.new";
  private String removedRoomsTopic = "rooms.removed";
  private EventBus eventBus;

  public ChatRoomsBroker(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public ChatRoomsBroker(String roomsAddedTopic, String roomsRemovedTopic, EventBus eventBus) {
    this.newRoomTopic = roomsAddedTopic;
    this.removedRoomsTopic = roomsRemovedTopic;
    this.eventBus = eventBus;
  }

  public void newRoom(String roomId) {
    this.eventBus.publish(newRoomTopic, new RoomMessage(roomId, RoomMessage.RoomAction.New));
  }

  public void removedRoom(String roomId) {
    this.eventBus.publish(removedRoomsTopic, new RoomMessage(roomId, RoomMessage.RoomAction.Removed));

  }
}
