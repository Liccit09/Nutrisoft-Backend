package com.nutrisoft.core.port.out.eventbus;

import java.util.List;

import com.nutrisoft.core.shared.ddd.DomainEvent;

/**
 * Event Bus Port - Primary Port.
 *
 * <p>Defines the contract for publishing domain events. This port allows components to publish
 * events to be consumed by other components asynchronously.
 *
 * <p>Located in: Core\Ports\In\EventBus
 *
 * <p>This is part of the hexagonal architecture's dependency inversion:
 * - Core depends on ports
 * - Infrastructure implements ports
 */
public interface EventBus {

  default void publish(final List<DomainEvent> events) {
    events.forEach(this::publish);
  }

  /**
   * Publish a domain event to be consumed asynchronously.
   *
   * @param event The domain event to publish
   */
  void publish(DomainEvent event);
}

