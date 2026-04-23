package com.nutrisoft.core.component.professional.domain;

import com.nutrisoft.core.shared.component.common.ContactInfo;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import com.nutrisoft.core.shared.ddd.AggregateRoot;
import lombok.Getter;
import lombok.NonNull;

/**
 * Professional Entity within the Appointment Component. This entity represents a nutrition
 * professional from the perspective of the Appointment domain.
 *
 * <p>Located in: Core\Components\Appointment\Domain
 *
 * <p>NOTE: In the future, when Professional becomes its own Bounded Context, this will be replaced
 * with a Professional ID reference (Anti-Corruption Layer).
 */
@Getter
public class Professional extends AggregateRoot<ProfessionalId> {

  private final String firstName;
  private final String lastName;
  private final String specialization;
  private final ContactInfo contactInfo;

  public Professional(
      @NonNull final ProfessionalId id,
      @NonNull final String firstName,
      @NonNull final String lastName,
      @NonNull final String specialization,
      @NonNull final ContactInfo contactInfo) {
    super(id);
    this.firstName = firstName;
    this.lastName = lastName;
    this.specialization = specialization;
    this.contactInfo = contactInfo;
  }

  /**
   * Create a new Professional.
   *
   * @param firstName First name of the professional
   * @param lastName Last name of the professional
   * @param specialization Specialization area (e.g., Clinical Nutrition, Sports Nutrition)
   * @param contactInfo Contact information (phone, email)
   * @return New Professional instance
   */
  public static Professional create(
      @NonNull final String firstName,
      @NonNull final String lastName,
      @NonNull final String specialization,
      @NonNull final ContactInfo contactInfo) {

    return new Professional(
        ProfessionalId.create(),
        firstName.trim(),
        lastName.trim(),
        specialization.trim(),
        contactInfo);
  }
}
