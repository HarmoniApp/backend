package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.UserLanguageDto;
import org.harmoniapp.harmoniwebapi.services.UserLanguageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-language")
public class UserLanguageController {
    private final UserLanguageService service;

    @GetMapping("")
    public List<UserLanguageDto> getUsers(@RequestParam(required = false, defaultValue = "0") int page) {
        return service.getUsersPage(page);
    }

    @GetMapping("/{id}")
    public UserLanguageDto getUser(@PathVariable long id) {
        return service.getUser(id);
    }
}
