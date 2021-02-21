package fi.johannes.health;

import fi.johannes.health.types.Latency;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Johannes on 20.5.2020.
 */
@ExtendWith(VertxExtension.class)
public class EventBusLatencyTest {

  @Test
  void testLatency(Vertx vertx, VertxTestContext testContext) throws InterruptedException {
    EventBusLatency lt = new EventBusLatency(vertx.eventBus());

    lt.latency().future().onComplete( (AsyncResult<Latency> v) -> {
      if(v.succeeded()) {
        Latency result = v.result();
        assertTrue(result.getDuration() > 0);
        assertTrue(result.getStart() > 0);
        testContext.completeNow();
      } else {
        testContext.failNow(v.cause());
      }
    });

    testContext.awaitCompletion(5, TimeUnit.SECONDS);
  }
}