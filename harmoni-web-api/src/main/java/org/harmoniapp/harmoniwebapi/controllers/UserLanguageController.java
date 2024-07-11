package org.harmoniapp.harmoniwebapi.controllers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.UserLanguageDto;
import org.harmoniapp.harmoniwebapi.services.UserLanguageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-language")
public class UserLanguageController {
    private final UserLanguageService service;

    @GetMapping
    public List<UserLanguageDto> getUsers() {
        return service.getUsers();
    }

    @GetMapping("/{id}")
    public UserLanguageDto getUser(@RequestParam long id) {
        return service.getUser(id);
    }
}
