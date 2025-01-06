package org.harmoniapp.contracts.user;

import jakarta.validation.constraints.NotEmpty;

/**
 * A record representing a new password for a user.
 *
 * @param newPassword the new password for the user
 */
public record UserNewPasswordDto(
        @NotEmpty(message = "New password cannot be empty")
        String newPassword) {
}
