package org.harmoniapp.contracts.profile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.harmoniapp.entities.profile.Role;

/**
 * Data Transfer Object for Role.
 *
 * @param id   the unique identifier of the role
 * @param name the name of the role
 */
public record RoleDto(
        long id,

        @NotEmpty(message = "Nazwa roli nie może być pusta")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]+$", message = "Nazwa roli musi zawierać tylko litery, cyfry i spacje")
        String name,

        @Size(min = 7, max = 7, message = "Kolor musi być poprawnym kodem heksadecymalnym w formacie #RRGGBB")
        @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Kolor musi być poprawnym kodem heksadecymalnym w formacie #RRGGBB")
        String color) {

    /**
     * Converts a Role entity to a RoleDto.
     *
     * @param role the Role entity to convert
     * @return the resulting RoleDto
     */
    public static RoleDto fromEntity(Role role) {
        return new RoleDto(role.getId(), role.getName(), role.getColor());
    }

    /**
     * Converts a RoleDto to a Role entity.
     *
     * @return the resulting Role entity
     */
    public Role toEntity() {
        return new Role(this.id, this.name, this.color);
    }
}
