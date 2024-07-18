package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.enums.Role;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Shift.
 *
 * @param id    the unique identifier of the shift
 * @param start the start time of the shift
 * @param end   the end time of the shift
 * @param userId  the userId associated with the shift
 * @param role  the role of the user during the shift
 */
public record ShiftDto(long id, LocalDateTime start, LocalDateTime end, @JsonProperty("user_id") long userId, Role role) {

    /**
     * Converts a Shift entity to a ShiftDto.
     *
     * @param shift the Shift entity to convert
     * @return the resulting ShiftDto
     */
    public static ShiftDto toDto(Shift shift) {
        return new ShiftDto(
                shift.getId(),
                shift.getStart(),
                shift.getEnd(),
                shift.getUser().getId(),
                shift.getRole()
        );
    }
}
