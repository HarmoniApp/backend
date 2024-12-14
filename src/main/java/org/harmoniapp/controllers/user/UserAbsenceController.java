package org.harmoniapp.controllers.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.services.user.UserAbsenceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserAbsenceController {
    private final UserAbsenceService service;

    @GetMapping("{id}/availableAbsenceDays")
    public int getAvailableAbsenceDays(@PathVariable Long id) {
        return service.getUserAvailableAbsenceDays(id);
    }
}
