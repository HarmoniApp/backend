package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.User;

import java.util.ArrayList;
import java.util.List;

public record SupervisorDto(long id, String firstname, String surname, List<RoleDto> role, @JsonProperty("employee-id") String employeeId) {

    public static SupervisorDto fromEntity(User user) {
        List<RoleDto> roles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            if (role.getId() <= 4) {
                roles.add(RoleDto.fromEntity(role));
            }
        }

        return new SupervisorDto(
                user.getId(),
                user.getFirstname(),
                user.getSurname(),
                roles.stream().toList(),
                user.getEmployeeId());
    }
}
