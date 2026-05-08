package com.nutrisoft.infrastructure.persistence.jpa.schedule.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nutrisoft.core.component.schedule.domain.BreakSlot;
import com.nutrisoft.core.component.schedule.domain.DayOfWeek;
import com.nutrisoft.core.component.schedule.domain.Schedule;
import com.nutrisoft.core.component.schedule.domain.SpecialDay;
import com.nutrisoft.core.component.schedule.domain.WorkingHours;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import com.nutrisoft.core.shared.component.schedule.ScheduleId;
import com.nutrisoft.infrastructure.persistence.jpa.schedule.entity.ScheduleEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mapper for Schedule entity conversions.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Schedule\Mapper
 *
 * <p>Converts between JPA entities and domain objects. Handles JSON serialization/deserialization
 * for complex nested structures.
 */
@Slf4j
@Component
public class ScheduleMapper {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  /**
   * Converts a Schedule domain object to a JPA entity.
   *
   * @param schedule The domain object
   * @return The JPA entity
   */
  public ScheduleEntity toEntity(final Schedule schedule) {
    try {
      final var weeklyScheduleJson = serializeWeeklySchedule(schedule.getWeeklyScheduleView());
      final var breaksJson = serializeBreaks(schedule.getBreaksView());
      final var specialDaysJson = serializeSpecialDays(schedule.getSpecialDaysView());

      return ScheduleEntity.builder()
          .id(schedule.getId().value())
          .professionalId(schedule.getProfessionalId().value())
          .weeklyScheduleJson(weeklyScheduleJson)
          .breaksJson(breaksJson)
          .specialDaysJson(specialDaysJson)
          .createdAt(schedule.getCreatedAt())
          .updatedAt(schedule.getUpdatedAt())
          .build();
    } catch (final JsonProcessingException e) {
      log.error("Error converting Schedule to entity", e);
      throw new RuntimeException("Failed to serialize schedule", e);
    }
  }

  /**
   * Converts a JPA entity to a Schedule domain object.
   *
   * @param entity The JPA entity
   * @return The domain object
   */
  public Schedule toDomain(final ScheduleEntity entity) {
    try {
      final var weeklySchedule = deserializeWeeklySchedule(entity.getWeeklyScheduleJson());
      final var breaks = deserializeBreaks(entity.getBreaksJson());
      final var specialDays = deserializeSpecialDays(entity.getSpecialDaysJson());

      return new Schedule(
          ScheduleId.of(entity.getId()),
          ProfessionalId.of(entity.getProfessionalId()),
          weeklySchedule,
          breaks,
          specialDays,
          entity.getCreatedAt(),
          entity.getUpdatedAt());
    } catch (final JsonProcessingException e) {
      log.error("Error converting entity to Schedule", e);
      throw new RuntimeException("Failed to deserialize schedule", e);
    }
  }

  private String serializeWeeklySchedule(final List<DayOfWeek> schedule)
      throws JsonProcessingException {
    final var dtos = schedule.stream()
        .map(day -> new DayOfWeekDto(
            day.getDay().name(),
            day.getWorkingHours().getStartTime().toString(),
            day.getWorkingHours().getEndTime().toString(),
            day.isWorkingDay()))
        .collect(Collectors.toList());

    return objectMapper.writeValueAsString(dtos);
  }

  private List<DayOfWeek> deserializeWeeklySchedule(final String json)
      throws JsonProcessingException {
    if (json == null || json.isEmpty()) {
      return createDefaultWeeklySchedule();
    }

    final var dtos = objectMapper.readValue(
        json,
        new TypeReference<List<DayOfWeekDto>>() {});

    return dtos.stream()
        .map(dto -> {
          final var dayOfWeek = java.time.DayOfWeek.valueOf(dto.day);
          if (dto.isWorkingDay) {
            final var startTime = LocalTime.parse(dto.startTime);
            final var endTime = LocalTime.parse(dto.endTime);
            return DayOfWeek.createWorkingDay(dayOfWeek, new WorkingHours(startTime, endTime));
          } else {
            return DayOfWeek.createNonWorkingDay(dayOfWeek);
          }
        })
        .collect(Collectors.toList());
  }

  private String serializeBreaks(final List<BreakSlot> breaks)
      throws JsonProcessingException {
    final var dtos = breaks.stream()
        .map(breakSlot -> new BreakSlotDto(
            breakSlot.getStartTime().toString(),
            breakSlot.getEndTime().toString(),
            breakSlot.getReason()))
        .collect(Collectors.toList());

    return objectMapper.writeValueAsString(dtos);
  }

  private List<BreakSlot> deserializeBreaks(final String json)
      throws JsonProcessingException {
    if (json == null || json.isEmpty()) {
      return new ArrayList<>();
    }

    final var dtos = objectMapper.readValue(
        json,
        new TypeReference<List<BreakSlotDto>>() {});

    return dtos.stream()
        .map(dto -> new BreakSlot(
            LocalDateTime.parse(dto.startTime),
            LocalDateTime.parse(dto.endTime),
            dto.reason))
        .collect(Collectors.toList());
  }

  private String serializeSpecialDays(final List<SpecialDay> specialDays)
      throws JsonProcessingException {
    final var dtos = specialDays.stream()
        .map(specialDay -> new SpecialDayDto(
            specialDay.getDate().toString(),
            specialDay.getReason(),
            specialDay.isWorkingDay()))
        .collect(Collectors.toList());

    return objectMapper.writeValueAsString(dtos);
  }

  private List<SpecialDay> deserializeSpecialDays(final String json)
      throws JsonProcessingException {
    if (json == null || json.isEmpty()) {
      return new ArrayList<>();
    }

    final var dtos = objectMapper.readValue(
        json,
        new TypeReference<List<SpecialDayDto>>() {});

    return dtos.stream()
        .map(dto -> new SpecialDay(
            LocalDate.parse(dto.date),
            dto.reason,
            dto.isWorkingDay))
        .collect(Collectors.toList());
  }

  private List<DayOfWeek> createDefaultWeeklySchedule() {
    final List<DayOfWeek> schedule = new ArrayList<>();
    final var workingHours = new WorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0));

    schedule.add(DayOfWeek.createNonWorkingDay(java.time.DayOfWeek.SUNDAY));
    schedule.add(DayOfWeek.createWorkingDay(java.time.DayOfWeek.MONDAY, workingHours));
    schedule.add(DayOfWeek.createWorkingDay(java.time.DayOfWeek.TUESDAY, workingHours));
    schedule.add(DayOfWeek.createWorkingDay(java.time.DayOfWeek.WEDNESDAY, workingHours));
    schedule.add(DayOfWeek.createWorkingDay(java.time.DayOfWeek.THURSDAY, workingHours));
    schedule.add(DayOfWeek.createWorkingDay(java.time.DayOfWeek.FRIDAY, workingHours));
    schedule.add(DayOfWeek.createNonWorkingDay(java.time.DayOfWeek.SATURDAY));

    return schedule;
  }

  // Helper DTOs for serialization
  private record DayOfWeekDto(String day, String startTime, String endTime, boolean isWorkingDay) {}

  private record BreakSlotDto(String startTime, String endTime, String reason) {}

  private record SpecialDayDto(String date, String reason, boolean isWorkingDay) {}
}
