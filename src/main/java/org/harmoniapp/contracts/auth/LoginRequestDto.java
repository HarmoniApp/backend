package org.harmoniapp.contracts.auth;

import jakarta.validation.constraints.NotEmpty;

/**
 * Data Transfer Object (DTO) for login request.
 *
 * @param username the username or email of the user attempting to log in.
 * @param password the password of the user attempting to log in.
 */
public record LoginRequestDto(
        @NotEmpty(message = "Login nie może być pusty") String username,
        @NotEmpty(message = "Hasło nie może być puste") String password) {
}
