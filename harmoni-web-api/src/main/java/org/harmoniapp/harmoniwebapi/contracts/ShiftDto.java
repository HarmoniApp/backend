package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.entities.User;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Shift.
 *
 * @param id      the unique identifier of the shift
 * @param start   the start time of the shift
 * @param end     the end time of the shift
 * @param userId  the userId associated with the shift
 * @param roleId    the roleId of the user during the shift
 */
public record ShiftDto(
        Long id,

        @NotNull(message = "Start time cannot be null")
        LocalDateTime start,

        @NotNull(message = "End time cannot be null")
        LocalDateTime end,

        @NotNull(message = "User ID cannot be null")
        @JsonProperty("user_id") Long userId,

        @NotNull(message = "Role ID cannot be null")
        @JsonProperty("role_id") Long roleId,

        boolean published) {

    /**
     * Converts a Shift entity to a ShiftDto.
     *
     * @param shift the Shift entity to convert
     * @return the resulting ShiftDto
     */
    public static ShiftDto fromEntity(Shift shift) {
        return new ShiftDto(
                shift.getId(),
                shift.getStart(),
                shift.getEnd(),
                shift.getUser().getId(),
                shift.getRole().getId(),
                shift.isPublished()
        );
    }

    /**
     * Converts a ShiftDto to a Shift entity.
     *
     * @param user the User entity associated with the shift
     * @return the resulting Shift entity
     */
    public Shift toEntity(User user, Role role) {
        return new Shift(
                this.id,
                this.start,
                this.end,
                user,
                role,
                this.published
        );
    }
}
