package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.SupervisorDto;
import org.harmoniapp.harmoniwebapi.services.SupervisorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user/supervisor")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SupervisorController {
    private final SupervisorService service;

    @GetMapping
    public List<SupervisorDto> getAllSupervisors() {
        return service.getAllSupervisors();
    }
}