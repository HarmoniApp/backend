package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.entities.Role;

import java.util.List;

public record SupervisorDto(long id,
                            String firstname,
                            String surname,
                            List<RoleDto> role,
                            @JsonProperty("employee_id") String employeeId) {

    public static SupervisorDto fromEntity(User user) {
        List<RoleDto> roles = user.getRoles().stream()
                .filter(Role::isSup)
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
