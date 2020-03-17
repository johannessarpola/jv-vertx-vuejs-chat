package fi.bilot.sse.utils;

/**
 *
 */
public interface EventStreamingProducer {

  default String sseFormat(String data) {
    return String.format("event: message\ndata:%s\n\n", data);
  }

  default String poisonPill() {
    return String.format("event: poison\ndata:\n\n");
  }

}
