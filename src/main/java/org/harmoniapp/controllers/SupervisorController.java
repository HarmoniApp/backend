package org.harmoniapp.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.SupervisorDto;
import org.harmoniapp.services.SupervisorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing supervisors.
 * This controller provides an endpoint to retrieve all supervisors.
 */
@RestController
@RequestMapping("user/supervisor")
@RequiredArgsConstructor
public class SupervisorController {
    private final SupervisorService service;

    /**
     * Retrieves a paginated list of all supervisors.
     *
     * @param pageNumber the page number to retrieve (optional, default is 1).
     * @param pageSize   the number of items per page (optional, default is 20).
     * @return a PageDto containing a list of SupervisorDto objects representing all supervisors
     */
    @GetMapping
    public PageDto<SupervisorDto> getAllSupervisors(@RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                                    @RequestParam(name = "pageSize", required = false, defaultValue = "20") int pageSize) {
        return service.getAllSupervisors(pageNumber, pageSize);
    }
}