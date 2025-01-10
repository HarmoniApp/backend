package org.harmoniapp.contracts.schedule.aischedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Data Transfer Object for Shift.
 *
 * @param shiftId the ID of the shift
 * @param roles   the list of roles for the shift
 */
public record ReqShiftDto(
        @NotNull(message = "ID zmiany nie może być puste")
        @Positive(message = "ID zmiany musi być liczbą dodatnią")
        Long shiftId,

        @NotEmpty(message = "Role nie mogą być puste")
        @Valid
        List<ReqRoleDto> roles) {
}
