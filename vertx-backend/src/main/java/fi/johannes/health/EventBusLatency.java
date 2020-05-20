package fi.johannes.health;

import fi.johannes.health.types.Latency;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;

/**
 * Johannes on 20.5.2020.
 */
public class EventBusLatency {

  private String latencyAddress = "health.latency";
  private EventBus eventBus;

  public EventBusLatency(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public Promise<Latency> latency() {
    Promise<Latency> promise = Promise.promise();
    MessageConsumer<Long> consumer = this.eventBus.<Long>consumer(latencyAddress, (msg) -> {
      promise.complete(new Latency(System.currentTimeMillis() - msg.body(), msg.body()));
    });
    Long currentTime = System.currentTimeMillis();
    this.eventBus.publish(latencyAddress, currentTime);
    promise.future().onComplete((m) -> {
      consumer.unregister();
    });
    return promise;
  }

}
