package org.harmoniapp.harmoniwebapi.contracts.AiSchedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ReqShiftDto(
        @NotNull(message = "Shift ID cannot be null")
        @Positive(message = "Shift ID must be a positive number")
        Long shiftId,

        @NotEmpty(message = "Roles cannot be empty")
        @Valid
        List<ReqRoleDto> roles
) {
}
