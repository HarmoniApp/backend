package org.harmoniapp.controllers.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.SupervisorDto;
import org.harmoniapp.services.user.SupervisorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * @param pageRequest the page request containing pagination information.
     * @return a PageDto containing a list of SupervisorDto objects representing all supervisors.
     */
    @GetMapping
    public PageDto<SupervisorDto> getAllSupervisors(@ModelAttribute PageRequestDto pageRequest) {
        return service.getAllSupervisors(pageRequest);
    }
}