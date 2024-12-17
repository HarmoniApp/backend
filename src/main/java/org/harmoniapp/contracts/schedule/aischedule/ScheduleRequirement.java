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
        @NotNull(message = "Date cannot be null")
        @Future(message = "Date must be in the future")
        LocalDate date,

        @NotEmpty(message = "Shifts cannot be empty")
        @Valid
        List<ReqShiftDto> shifts
) {
}
