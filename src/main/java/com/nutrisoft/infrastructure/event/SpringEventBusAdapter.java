package com.nutrisoft.infrastructure.event;

import com.nutrisoft.core.port.out.eventbus.EventBus;
import com.nutrisoft.core.shared.ddd.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Event Bus Adapter - Using Spring ApplicationEventPublisher.
 *
 * <p>This adapter implements the EventBus port using Spring's native event publishing mechanism.
 * It leverages Spring's ApplicationEventPublisher to publish domain events which can be consumed
 * by @EventListener methods throughout the application.
 *
 * <p>Located in: Infrastructure\Event
 *
 * <p>Implementation Details:
 * - Uses Spring's ApplicationEventPublisher for event publishing
 * - Supports asynchronous event handling with @Async
 * - Integrates with Spring's transaction management
 * - Can use @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringEventBusAdapter implements EventBus {

  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void publish(final DomainEvent event) {
    log.info("Publishing domain event: {} for aggregate: {}", event.getEventType(), event.getAggregateId());
    eventPublisher.publishEvent(event);
    log.debug("Domain event published successfully: {}", event);
  }
}
