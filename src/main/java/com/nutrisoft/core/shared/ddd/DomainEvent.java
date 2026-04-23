package com.nutrisoft.core.shared.ddd;

import java.time.LocalDateTime;

/**
 * Base interface for Domain Events. Domain events are used to capture important occurrences within
 * the domain. They are immutable and represent a change that occurred in the past.
 *
 * <p>Located in: Shared Kernel - Core Layer Events are shared by components and can be listened to
 * by multiple components
 */
public interface DomainEvent {

  /**
   * Get the timestamp when the event occurred.
   *
   * @return The event timestamp
   */
  LocalDateTime getOccurredAt();

  /**
   * Get the type/name of the domain event.
   *
   * @return The event type name
   */
  String getEventType();

  /**
   * Get the aggregate ID that triggered this event.
   *
   * @return The aggregate ID
   */
  String getAggregateId();
}
