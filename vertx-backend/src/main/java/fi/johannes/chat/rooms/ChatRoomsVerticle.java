package fi.johannes.chat.rooms;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Should handle persisting rooms to Mongo and managing users through it.
 * - Handle authentication
 * - Storing rooms and their connected users
 * - Manage rooms
 *
 * TODO: Maybe not a verticle?
 * Johannes on 20.5.2020.
 */origi
public class ChatRoomsVerticle extends AbstractVerticle {


  @Override
  public void start(Promise<Void> startPromise) throws Exception {

  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {

  }

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

}
