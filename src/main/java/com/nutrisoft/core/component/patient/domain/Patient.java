package com.nutrisoft.core.component.patient.domain;

import com.nutrisoft.core.shared.component.common.ContactInfo;
import com.nutrisoft.core.shared.component.patient.PatientId;
import com.nutrisoft.core.shared.ddd.AggregateRoot;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NonNull;

/**
 * Patient Entity within the Appointment Component. This entity represents a patient from the
 * perspective of the Appointment domain.
 *
 * <p>Located in: Core\Components\Appointment\Domain
 *
 * <p>NOTE: In the future, when Patient becomes its own Bounded Context, this will be replaced with
 * a Patient ID reference (Anti-Corruption Layer).
 */
@Getter
public class Patient extends AggregateRoot<PatientId> {

  private final String firstName;
  private final String lastName;
  private final LocalDate dateOfBirth;
  private final ContactInfo contactInfo;
  private final String address;

  public Patient(
      @NonNull final PatientId id,
      @NonNull final String firstName,
      @NonNull final String lastName,
      final LocalDate dateOfBirth,
      @NonNull final ContactInfo contactInfo,
      final String address) {
    super(id);
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.contactInfo = contactInfo;
    this.address = address;
  }

  public static Patient create(
      @NonNull final String firstName,
      @NonNull final String lastName,
      final LocalDate dateOfBirth,
      @NonNull final ContactInfo contactInfo,
      final String address) {

    return new Patient(
        PatientId.create(), firstName.trim(), lastName.trim(), dateOfBirth, contactInfo, address);
  }
}
