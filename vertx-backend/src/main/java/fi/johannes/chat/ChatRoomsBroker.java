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

  public void newRoom(String roomAddress) {
    System.out.println("New room: " + roomAddress);
    this.eventBus.publish(newRoomTopic, new RoomMessage(roomAddress, RoomMessage.RoomAction.New).jsonStr());
  }

  public void removedRoom(String roomAddress) {
    System.out.println("Removed room: " + roomAddress);
    this.eventBus.publish(removedRoomsTopic, new RoomMessage(roomAddress, RoomMessage.RoomAction.Removed).jsonStr());
  }
}
