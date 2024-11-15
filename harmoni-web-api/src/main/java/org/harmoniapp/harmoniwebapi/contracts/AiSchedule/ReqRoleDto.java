package org.harmoniapp.harmoniwebapi.contracts.AiSchedule;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReqRoleDto(
        @NotNull(message = "Role ID cannot be null")
        @Positive(message = "Role ID must be a positive number")
        Long roleId,

        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be a positive number")
        int quantity
) {
}


