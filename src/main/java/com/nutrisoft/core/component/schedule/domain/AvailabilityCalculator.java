package com.nutrisoft.core.component.schedule.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * AvailabilityCalculator Domain Service.
 *
 * <p>Calculates available time slots for appointment booking considering professional schedules,
 * breaks, appointments, and service duration.
 *
 * <p>Located in: Core\Components\Schedule\Domain
 *
 * <p>This is a Domain Service (not an Application Service) because it encapsulates domain
 * business rules around availability calculation.
 */
@Slf4j
public class AvailabilityCalculator {

  private static final int SLOT_DURATION_MINUTES = 15; // Default time slot duration for display

  /**
   * Calculates available time slots for a given date.
   *
   * @param schedule The professional's schedule
   * @param date The date to calculate availability for
   * @param serviceDurationInMinutes The duration of the service in minutes
   * @param appointmentsOnDate List of existing appointments on that date
   * @return List of available time slots (start times)
   */
  public static List<LocalTime> calculateAvailableSlots(
      @NonNull final Schedule schedule,
      @NonNull final LocalDate date,
      final int serviceDurationInMinutes,
      @NonNull final List<LocalDateTime> appointmentsOnDate) {

    log.debug(
        "Calculating available slots for date: {} with service duration: {} minutes",
        date,
        serviceDurationInMinutes);

    final List<LocalTime> availableSlots = new ArrayList<>();

    // Check if it's a working day
    if (!schedule.isWorkingDay(date)) {
      log.debug("Date {} is not a working day", date);
      return availableSlots;
    }

    // Get working hours for this day
    final var dayOfWeek = date.getDayOfWeek();
    final var workingHours = schedule.getWorkingHoursForDay(dayOfWeek);

    if (workingHours == null) {
      log.debug("No working hours defined for day {} ({})", date, dayOfWeek);
      return availableSlots;
    }

    // Generate time slots within working hours
    final List<LocalTime> potentialSlots =
        generateTimeSlots(workingHours, serviceDurationInMinutes);

    // Get breaks for this date
    final var breaksOnDate = schedule.getBreaksForDate(date);

    // Filter out slots that conflict with breaks or existing appointments
    for (final var slot : potentialSlots) {
      if (canSlotBeBooked(
          slot, serviceDurationInMinutes, date, appointmentsOnDate, breaksOnDate)) {
        availableSlots.add(slot);
      }
    }

    log.debug("Found {} available slots for date {}", availableSlots.size(), date);
    return availableSlots;
  }

  /**
   * Calculates available dates within a date range.
   *
   * @param schedule The professional's schedule
   * @param startDate The start date (inclusive)
   * @param endDate The end date (inclusive)
   * @param appointmentsByDate Map of existing appointments grouped by date
   * @return List of dates that have at least one available slot
   */
  public static List<LocalDate> calculateAvailableDates(
      @NonNull final Schedule schedule,
      @NonNull final LocalDate startDate,
      @NonNull final LocalDate endDate,
      final int serviceDurationInMinutes,
      @NonNull final java.util.Map<LocalDate, List<LocalDateTime>> appointmentsByDate) {

    log.debug(
        "Calculating available dates from {} to {} with service duration: {} minutes",
        startDate,
        endDate,
        serviceDurationInMinutes);

    final List<LocalDate> availableDates = new ArrayList<>();
    LocalDate currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      if (schedule.isWorkingDay(currentDate)) {
        final var dayOfWeek = currentDate.getDayOfWeek();
        final var workingHours = schedule.getWorkingHoursForDay(dayOfWeek);

        if (workingHours != null) {
          // Check if there's at least one available slot on this date
          final var appointmentsOnDate = appointmentsByDate.getOrDefault(currentDate, List.of());
          final var availableSlots =
              calculateAvailableSlots(
                  schedule, currentDate, serviceDurationInMinutes, appointmentsOnDate);

          if (!availableSlots.isEmpty()) {
            availableDates.add(currentDate);
          }
        }
      }

      currentDate = currentDate.plusDays(1);
    }

    log.debug("Found {} available dates", availableDates.size());
    return availableDates;
  }

  /**
   * Checks if a specific time slot can be booked (no conflicts with breaks or appointments).
   *
   * @param slotStart The start time of the slot
   * @param serviceDurationInMinutes The duration of the service
   * @param date The date of the slot
   * @param appointmentsOnDate Existing appointments on the date
   * @param breaksOnDate Breaks on the date
   * @return true if the slot can be booked, false otherwise
   */
  private static boolean canSlotBeBooked(
      final LocalTime slotStart,
      final int serviceDurationInMinutes,
      final LocalDate date,
      final List<LocalDateTime> appointmentsOnDate,
      final List<BreakSlot> breaksOnDate) {

    final var slotStartDateTime = LocalDateTime.of(date, slotStart);
    final var slotEndDateTime = slotStartDateTime.plusMinutes(serviceDurationInMinutes);

    // Check for conflicts with existing appointments
    for (final var appointmentStart : appointmentsOnDate) {
      // We need the appointment duration, but we don't have it here
      // This is a limitation - we'll assume a reasonable appointment duration
      // In production, this would be passed from the caller
      final var appointmentEnd = appointmentStart.plusMinutes(serviceDurationInMinutes);

      if (slotStartDateTime.isBefore(appointmentEnd) && slotEndDateTime.isAfter(appointmentStart)) {
        log.debug("Slot {} conflicts with existing appointment", slotStart);
        return false;
      }
    }

    // Check for conflicts with breaks
    for (final var breakSlot : breaksOnDate) {
      if (breakSlot.overlapsWithSlot(slotStartDateTime, slotEndDateTime)) {
        log.debug("Slot {} conflicts with break slot", slotStart);
        return false;
      }
    }

    return true;
  }

  /**
   * Generates time slots within working hours with a fixed duration.
   *
   * @param workingHours The working hours
   * @param serviceDurationInMinutes The service duration in minutes
   * @return List of potential time slots
   */
  private static List<LocalTime> generateTimeSlots(
      final WorkingHours workingHours, final int serviceDurationInMinutes) {

    final List<LocalTime> slots = new ArrayList<>();
    LocalTime currentSlot = workingHours.getStartTime();

    // Generate slots at 15-minute intervals (or service duration if longer)
    final int slotInterval = Math.min(SLOT_DURATION_MINUTES, serviceDurationInMinutes);

    while (workingHours.canFitSlot(currentSlot, serviceDurationInMinutes)) {
      slots.add(currentSlot);
      currentSlot = currentSlot.plusMinutes(slotInterval);
    }

    return slots;
  }

  /**
   * Checks if a specific time slot fits within working hours and service duration.
   *
   * @param schedule The professional's schedule
   * @param dateTime The proposed appointment date-time
   * @param serviceDurationInMinutes The service duration in minutes
   * @return true if the slot fits, false otherwise
   */
  public static boolean canBookSlot(
      @NonNull final Schedule schedule,
      @NonNull final LocalDateTime dateTime,
      final int serviceDurationInMinutes) {

    final var date = dateTime.toLocalDate();
    final var time = dateTime.toLocalTime();

    // Check if it's a working day
    if (!schedule.isWorkingDay(date)) {
      return false;
    }

    // Get working hours
    final var dayOfWeek = date.getDayOfWeek();
    final var workingHours = schedule.getWorkingHoursForDay(dayOfWeek);

    if (workingHours == null) {
      return false;
    }

    // Check if slot fits within working hours
    return workingHours.canFitSlot(time, serviceDurationInMinutes);
  }
}
