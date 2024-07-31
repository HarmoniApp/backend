package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.PartialUserDto;
import org.harmoniapp.harmoniwebapi.services.PartialUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user languages.
 * Provides endpoints to retrieve user information and their associated languages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("user/simple")
public class PartialUserController {
    private final PartialUserService service;

    /**
     * Retrieves a paginated list of users and their associated languages.
     *
     * @param page the page number to retrieve (default is 0)
     * @return a list of UserLanguageDto containing user information and their languages
     */
    @GetMapping("")
    public List<PartialUserDto> getUsers(@RequestParam(required = false, defaultValue = "0") int page) {
        return service.getUsersPage(page);
    }


    /**
     * Retrieves a user's information and their associated languages by user ID.
     *
     * @param id the ID of the user to retrieve
     * @return a UserLanguageDto containing user information and their languages
     */
    @GetMapping("/{id}")
    public PartialUserDto getUser(@PathVariable long id) {
        return service.getUser(id);
    }
}
