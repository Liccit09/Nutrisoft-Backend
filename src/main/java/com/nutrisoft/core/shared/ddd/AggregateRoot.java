package com.nutrisoft.core.shared.ddd;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for Aggregate Roots. Aggregates are clusters of associated objects that we treat as a
 * unit for the purpose of data changes. The root entity is the only member of the aggregate that
 * outside objects are allowed to hold references to.
 *
 * <p>Located in: Shared Kernel - Core Layer This class is shared by all components within the Core
 *
 * @param <I> The type of the aggregate identifier
 */
public abstract class AggregateRoot<I extends AggregateRootId<?>> extends DomainEntity<I> {

  private final List<DomainEvent> domainEvents = new ArrayList<>();

  protected AggregateRoot(I id) {
    super(id);
  }

  /**
   * Add a domain event to the aggregate. Events should be published after aggregate persistence.
   *
   * @param event The event to register
   */
  protected void registerEvent(DomainEvent event) {
    this.domainEvents.add(event);
  }

  /**
   * Get all domain events registered in this aggregate.
   *
   * @return List of domain events
   */
  public List<DomainEvent> getUncommittedEvents() {
    return new ArrayList<>(this.domainEvents);
  }

  /** Clear all registered domain events after publishing. */
  public void markEventsAsPublished() {
    this.domainEvents.clear();
  }
}
