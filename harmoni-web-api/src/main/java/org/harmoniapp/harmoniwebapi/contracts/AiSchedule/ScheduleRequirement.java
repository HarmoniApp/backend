package org.harmoniapp.harmoniwebapi.contracts.AiSchedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ScheduleRequirement(
        @NotNull(message = "Date cannot be null")
        @Future(message = "Date must be in the future")
        LocalDate date,

        @NotEmpty(message = "Shifts cannot be empty")
        @Valid
        List<ReqShiftDto> shifts
) {
}
