package org.harmoniapp.harmoniwebapi.contracts;

import org.harmoniapp.harmonidata.entities.Role;

/**
 * Data Transfer Object for Role.
 *
 * @param id   the unique identifier of the role
 * @param name the name of the role
 */
public record RoleDto(long id, String name) {

    /**
     * Converts a Role entity to a RoleDto.
     *
     * @param role the Role entity to convert
     * @return the resulting RoleDto
     */
    public static RoleDto fromEntity(Role role) {
        return new RoleDto(
                role.getId(),
                role.getName());
    }

    /**
     * Converts a RoleDto to a Role entity.
     *
     * @return the resulting Role entity
     */
    public Role toEntity() {
        return new Role(
                this.id,
                this.name
        );
    }
}
