package org.harmoniapp.contracts.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.entities.user.User;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a supervisor.
 * This record encapsulates essential information about a supervisor.
 *
 * @param id         The unique identifier of the supervisor.
 * @param firstname  The first name of the supervisor.
 * @param surname    The surname of the supervisor.
 * @param role       The list of roles associated with the supervisor.
 * @param employeeId The employee ID of the supervisor.
 */
@Builder
public record SupervisorDto(long id,
                            String firstname,
                            String surname,
                            List<RoleDto> role,
                            @JsonProperty("employee_id") String employeeId) {

    /**
     * Converts a {@link User} entity to a {@link SupervisorDto}.
     * Filters roles based on the role name being "admin" and maps them to {@link RoleDto}.
     *
     * @param user the {@link User} entity to convert.
     * @return a {@link SupervisorDto} representing the user entity.
     */
    public static SupervisorDto fromEntity(User user) {
        return SupervisorDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .surname(user.getSurname())
                .role(getRoles(user))
                .employeeId(user.getEmployeeId())
                .build();
    }

    private static List<RoleDto> getRoles(User user) {
        return user.getRoles().stream()
                .map(RoleDto::fromEntity)
                .toList();
    }
}
