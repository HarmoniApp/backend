package org.harmoniapp.contracts.schedule.aischedule;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object for Role.
 *
 * @param roleId   the ID of the role
 * @param quantity the quantity of the role
 */
public record ReqRoleDto(
        @NotNull(message = "Role ID cannot be null")
        @Positive(message = "Role ID must be a positive number")
        Long roleId,

        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be a positive number")
        int quantity
) {
}


