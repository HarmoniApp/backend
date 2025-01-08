package org.harmoniapp.contracts.user;

import jakarta.validation.constraints.NotBlank;

/**
 * A record representing a new password for a user.
 *
 * @param newPassword the new password for the user
 */
public record UserNewPasswordDto(
        @NotBlank(message = "Nowe hasło nie może być puste")
        String newPassword) {
}
