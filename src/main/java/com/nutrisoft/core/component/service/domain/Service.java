package com.nutrisoft.core.component.service.domain;

import com.nutrisoft.core.shared.component.service.ServiceId;
import com.nutrisoft.core.shared.ddd.DomainEntity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NonNull;

/**
 * Service Entity within the Appointment Component. Represents a nutrition service that can be
 * provided to patients.
 *
 * <p>Located in: Core\Components\Appointment\Domain
 *
 * <p>NOTE: In the future, when Service becomes its own Bounded Context, this will be replaced with
 * a Service ID reference.
 */
@Getter
public class Service extends DomainEntity<ServiceId> {

  private final String name;
  private final String description;
  private final BigDecimal price;
  private final Integer durationInMinutes;

  public Service(
      @NonNull final ServiceId id,
      @NonNull final String name,
      @NonNull final String description,
      @NonNull final BigDecimal price,
      @NonNull final Integer durationInMinutes) {
    super(id);
    this.name = name;
    this.description = description;
    this.price = price;
    this.durationInMinutes = durationInMinutes;
  }

  public static Service create(
      @NonNull final String name,
      @NonNull final String description,
      @NonNull final BigDecimal price,
      @NonNull final Integer durationInMinutes) {

    return new Service(ServiceId.create(), name.trim(), description, price, durationInMinutes);
  }
}
