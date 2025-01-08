package org.harmoniapp.contracts.schedule.aischedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
        LocalDate date,

        @NotEmpty(message = "Zmiany nie mogą być puste")
        @Valid
        List<ReqShiftDto> shifts) {
}
