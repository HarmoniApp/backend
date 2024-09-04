package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.harmoniapp.harmonidata.entities.Role;

/**
 * Data Transfer Object for Role.
 *
 * @param id    the unique identifier of the role
 * @param name  the name of the role
 * @param isSup indicates whether the role is a superior role.
 */
public record RoleDto(
        long id,

        @NotEmpty(message = "Role name cannot be empty")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Role name must contain only letters and digits")
        String name,

        @JsonProperty("is_sup") boolean isSup,

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
                role.isSup(),
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
                this.isSup,
                this.color
        );
    }
}
