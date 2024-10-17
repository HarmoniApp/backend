package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.PageDto;
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
@CrossOrigin(origins = "http://localhost:3000")
public class PartialUserController {
    private final PartialUserService service;

    /**
     * Retrieves a paginated list of PartialUserDto based on the specified filtering and sorting criteria.
     *
     * @param roles      an optional list of role IDs to filter the users by roles. If not specified, no role-based filtering is applied.
     * @param contracts  an optional list of contract IDs to filter the users by contracts. If not specified, no contract-based filtering is applied.
     * @param language   an optional list of language IDs to filter the users by languages. If not specified, no language-based filtering is applied.
     * @param pageNumber the page number to retrieve (optional, default is 1).
     * @param pageSize   the number of items per page (optional, default is 10).
     * @param sortBy     an optional field by which to sort the results. Default is "firstname".
     * @param order      an optional order of sorting, either "asc" for ascending or "desc" for descending. Default is "asc".
     * @return a PageDto containing a list of {@link PartialUserDto} objects that match the specified criteria.
     */
    @GetMapping("")
    public PageDto<PartialUserDto> getUsers(@RequestParam(name = "role", required = false) List<Long> roles,
                                            @RequestParam(name = "contract", required = false) List<Long> contracts,
                                            @RequestParam(name = "language", required = false) List<Long> language,
                                            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                                            @RequestParam(name = "sortBy", required = false, defaultValue = "firstname") String sortBy,
                                            @RequestParam(name = "order", required = false, defaultValue = "asc") String order) {
        return service.getUsers(roles, contracts, language, pageNumber, pageSize, sortBy, order);
    }


    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user.
     * @return the {@link PartialUserDto} object representing the user with the specified ID.
     */
    @GetMapping("/{id}")
    public PartialUserDto getUser(@PathVariable long id) {
        return service.getUser(id);
    }

    /**
     * Retrieves a list of users based on a search query.
     *
     * @param q the search query string to filter users by. This can match against various user attributes.
     * @return a list of {@link PartialUserDto} objects that match the search query.
     */
    @GetMapping("/search")
    public List<PartialUserDto> getUsersSearch(@RequestParam String q) {
        return service.getUsersSearch(q);
    }
}
