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
        @NotNull(message = "ID roli nie może być puste")
        @Positive(message = "ID roli musi być liczbą dodatnią")
        Long roleId,

        @NotNull(message = "Liczebność nie może być pusta")
        @Positive(message = "Liczebność musi być liczbą dodatnią")
        int quantity) {
}


