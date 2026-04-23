package com.nutrisoft.core.shared.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

/**
 * Utility mapper for DateTime conversions.
 *
 * <p>Located in: UserInterface\API\Rest\Appointment\Mapper
 *
 * <p>Responsibility: Provide reusable mapping methods for date/time conversions between API layer
 * (OffsetDateTime) and application/domain layer (LocalDateTime)
 *
 * <p>This mapper is used as a "uses" component in MapStruct mappers to ensure consistent date/time
 * handling across the entire mapping layer.
 */
@Component
public class DateTimeMapper {

  public LocalDateTime map(final OffsetDateTime offsetDateTime) {
    if (offsetDateTime == null) {
      return null;
    }
    return offsetDateTime.toLocalDateTime();
  }

  public OffsetDateTime map(final LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return null;
    }
    return localDateTime.atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime();
  }
}
