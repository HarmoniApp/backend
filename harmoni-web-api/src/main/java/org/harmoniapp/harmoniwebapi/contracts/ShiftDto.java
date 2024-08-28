package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.entities.User;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object for Shift.
 *
 * @param id      the unique identifier of the shift
 * @param start   the start time of the shift
 * @param end     the end time of the shift
 * @param userId  the userId associated with the shift
 * @param roleId    the roleId of the user during the shift
 */
public record ShiftDto(Long id, LocalDate start, LocalDate end, @JsonProperty("user_id") Long userId,  @JsonProperty("role_id") Long roleId) {

    /**
     * Converts a Shift entity to a ShiftDto.
     *
     * @param shift the Shift entity to convert
     * @return the resulting ShiftDto
     */
    public static ShiftDto fromEntity(Shift shift) {
        return new ShiftDto(
                shift.getId(),
                shift.getStart().toLocalDate(),
                shift.getEnd().toLocalDate(),
                shift.getUser().getId(),
                shift.getRole().getId()
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
                this.start.atStartOfDay(),
                this.end.atTime(LocalTime.MAX),
                user,
                role
        );
    }
}
