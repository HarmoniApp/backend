package org.harmoniapp.contracts.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
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

        @NotNull(message = "Start time cannot be null")
        LocalDateTime start,

        @NotNull(message = "End time cannot be null")
        LocalDateTime end,

        @NotNull(message = "User ID cannot be null")
        @Positive(message = "User ID must be a positive number")
        @JsonProperty("user_id") Long userId,

        @NotNull(message = "Role Name cannot be null")
        @NotEmpty(message = "Role Name cannot be empty")
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
                .userId(shift.getUser().getId())
                .roleName((shift.getRole() != null) ? shift.getRole().getName() : null)
                .published(shift.getPublished())
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
