package org.harmoniapp.controllers.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.user.UserNewPassword;
import org.harmoniapp.services.user.UserPasswordService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserPasswordController {
    private final UserPasswordService service;

    @PatchMapping("/{id}/changePassword")
    public String changePassword(@PathVariable long id, @RequestBody UserNewPassword password) {
        return service.changePassword(id, password);
    }

    @PatchMapping("/{id}/generatePassword")
    public String generateNewPassword(@PathVariable long id) {
        return service.generateNewPassword(id);
    }
}
