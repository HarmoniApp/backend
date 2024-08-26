package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.StatusDto;
import org.harmoniapp.harmoniwebapi.services.StatusService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing statuses.
 * Provides endpoints to retrieve status information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/status")
@CrossOrigin(origins = "http://localhost:3000")
public class StatusController {
    private final StatusService statusService;

    /**
     * Retrieves a list of all StatusDto.
     *
     * @return a list of StatusDto representing all statuses
     */
    @GetMapping
    public List<StatusDto> getAllStatuses() {
        return statusService.getAllStatuses();
    }
}
