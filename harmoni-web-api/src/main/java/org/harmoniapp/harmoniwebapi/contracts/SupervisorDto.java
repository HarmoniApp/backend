package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.entities.Role;

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
public record SupervisorDto(long id,
                            String firstname,
                            String surname,
                            List<RoleDto> role,
                            @JsonProperty("employee_id") String employeeId) {

    /**
     * Converts a {@link User} entity to a {@link SupervisorDto}.
     * Filters roles based on the {@link Role#getIsSup()} method and maps them to {@link RoleDto}.
     *
     * @param user the {@link User} entity to convert.
     * @return a {@link SupervisorDto} representing the user entity.
     */
    public static SupervisorDto fromEntity(User user) {
        List<RoleDto> roles = user.getRoles().stream()
                .filter(Role::getIsSup)
                .map(RoleDto::fromEntity)
                .toList();

        return new SupervisorDto(
                user.getId(),
                user.getFirstname(),
                user.getSurname(),
                roles,
                user.getEmployeeId());
    }
}
