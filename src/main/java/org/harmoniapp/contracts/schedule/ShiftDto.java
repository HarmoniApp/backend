package org.harmoniapp.contracts.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Shift.
 *
 * @param id        the unique identifier of the shift
 * @param start     the start time of the shift
 * @param end       the end time of the shift
 * @param userId    the userId associated with the shift
 * @param roleName  the roleName of the user during the shift
 * @param published the published status of the shift
 */
@Builder
public record ShiftDto(
        Long id,

        @NotNull(message = "Data początkowa nie może być pusta")
        LocalDateTime start,

        @NotNull(message = "Data końcowa nie może być pusta")
        LocalDateTime end,

        @NotNull(message = "ID użytkownika nie może być puste")
        @Positive(message = "ID użytkownika musi być liczbą dodatnią")
        @JsonProperty("user_id") Long userId,

        @JsonProperty("role_name") String roleName,

        boolean published) {

    /**
     * Converts a Shift entity to a ShiftDto.
     *
     * @param shift the Shift entity to convert
     * @return the resulting ShiftDto
     */
    public static ShiftDto fromEntity(Shift shift) {
        return ShiftDto.builder()
                .id(shift.getId())
                .start(shift.getStart())
                .end(shift.getEnd())
                .userId((shift.getUser() != null) ? shift.getUser().getId() : null)
                .roleName((shift.getRole() != null) ? shift.getRole().getName() : null)
                .published((shift.getPublished() != null) ? shift.getPublished() : false)
                .build();
    }

    /**
     * Converts a ShiftDto to a Shift entity.
     *
     * @param user the User entity associated with the shift
     * @return the resulting Shift entity
     */
    public Shift toEntity(User user, Role role) {
        return Shift.builder()
                .id(this.id)
                .start(this.start)
                .end(this.end)
                .user(user)
                .role(role)
                .published(this.published)
                .build();
    }
}
