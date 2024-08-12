package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.SupervisorDto;
import org.harmoniapp.harmoniwebapi.services.SupervisorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("user/supervisor")
@RequiredArgsConstructor
public class SupervisorController {
    private final SupervisorService service;

    @GetMapping
    public List<SupervisorDto> getAllSupervisors() {
        return service.getAllSupervisors();
    }
}
