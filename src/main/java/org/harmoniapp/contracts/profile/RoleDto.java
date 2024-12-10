package org.harmoniapp.contracts.profile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.harmoniapp.entities.profile.Role;

/**
 * Data Transfer Object for Role.
 *
 * @param id    the unique identifier of the role
 * @param name  the name of the role
 */
public record RoleDto(
        long id,

        @NotEmpty(message = "Role name cannot be empty")
        @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Role name must contain only letters, digits, and spaces")
        String name,

        @Size(min = 7, max = 7, message = "Color must be exactly 7 characters long")
        @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Color must be a valid hex code in the format #RRGGBB")
        String color) {

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
                role.getColor());
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
                this.color
        );
    }
}
