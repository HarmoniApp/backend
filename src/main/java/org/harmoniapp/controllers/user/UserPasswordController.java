package org.harmoniapp.controllers.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.user.UserNewPasswordDto;
import org.harmoniapp.services.user.UserPasswordService;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling user password-related operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserPasswordController {
    private final UserPasswordService service;

    /**
     * Changes the password for a user with the given ID.
     *
     * @param id The ID of the user.
     * @param password The new password details.
     * @return A confirmation message.
     */
    @PatchMapping("/{id}/changePassword")
    public String changePassword(@PathVariable long id, @Valid @RequestBody UserNewPasswordDto password) {
        return service.changePassword(id, password);
    }

    /**
     * Generates a new password for a user with the given ID.
     *
     * @param id The ID of the user.
     * @return The new password.
     */
    @PatchMapping("/{id}/generatePassword")
    public String generateNewPassword(@PathVariable long id) {
        return service.generateNewPassword(id);
    }
}
