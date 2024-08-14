package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.Role;

/**
 * Data Transfer Object for Role.
 *
 * @param id    the unique identifier of the role
 * @param name  the name of the role
 * @param isSup indicates whether the role is a superior role.
 */
public record RoleDto(long id, String name, @JsonProperty("is_sup") boolean isSup) {

    /**
     * Converts a Role entity to a RoleDto.
     *
     * @param role the Role entity to convert
     * @return the resulting RoleDto
     */
    public static RoleDto fromEntity(Role role) {
        return new RoleDto(
                role.getId(),
                role.getName(),
                role.isSup());
    }

    /**
     * Converts a RoleDto to a Role entity.
     *
     * @return the resulting Role entity
     */
    public Role toEntity() {
        return new Role(
                this.id,
                this.name,
                this.isSup
        );
    }
}
