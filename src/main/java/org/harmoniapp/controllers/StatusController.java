package org.harmoniapp.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.StatusDto;
import org.harmoniapp.services.StatusService;
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
