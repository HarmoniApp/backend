package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.SupervisorDto;
import org.harmoniapp.harmoniwebapi.services.SupervisorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing supervisors.
 * This controller provides an endpoint to retrieve all supervisors.
 */
@RestController
@RequestMapping("user/supervisor")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SupervisorController {
    private final SupervisorService service;

    /**
     * Retrieves all supervisors.
     *
     * @return a list of {@link SupervisorDto} representing all supervisors.
     */
    @GetMapping
    public List<SupervisorDto> getAllSupervisors() {
        return service.getAllSupervisors();
    }
}