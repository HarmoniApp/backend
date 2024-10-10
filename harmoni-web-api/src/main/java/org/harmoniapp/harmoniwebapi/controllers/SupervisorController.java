package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.PageDto;
import org.harmoniapp.harmoniwebapi.contracts.SupervisorDto;
import org.harmoniapp.harmoniwebapi.services.SupervisorService;
import org.springframework.web.bind.annotation.*;

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
     * Retrieves a paginated list of all supervisors.
     *
     * @param pageNumber the page number to retrieve, defaults to 0 if not specified
     * @param pageSize   the number of items per page, defaults to 20 if not specified
     * @return a PageDto containing a list of SupervisorDto objects representing all supervisors
     */
    @GetMapping
    public PageDto<SupervisorDto> getAllSupervisors(@RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                                    @RequestParam(name = "pageSize", required = false, defaultValue = "20") int pageSize) {
        return service.getAllSupervisors(pageNumber, pageSize);
    }
}