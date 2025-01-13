package org.harmoniapp.contracts.schedule.aischedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for ScheduleRequirement.
 *
 * @param date   the date of the schedule requirement
 * @param shifts the list of shifts for the schedule requirement
 */
public record ScheduleRequirement(
        @NotNull(message = "Data nie może być pusta")
        @Future(message = "Data musi być w przyszłości")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,

        @NotEmpty(message = "Zmiany nie mogą być puste")
        @Valid
        List<ReqShiftDto> shifts) {
}
