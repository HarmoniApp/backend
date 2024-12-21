package org.harmoniapp.controllers.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.PartialUserDto;
import org.harmoniapp.contracts.user.UserSearchParamsDto;
import org.harmoniapp.services.user.PartialUserService;
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
     * Retrieves a paginated list of PartialUserDto based on the specified filtering and sorting criteria.
     *
     * @param searchParamsDto the search parameters for filtering users.
     * @param pageRequestDto  the pagination and sorting parameters.
     * @return a PageDto containing a list of PartialUserDto objects that match the specified criteria.
     */
    @GetMapping(value = {"", "/empId"})
    public PageDto<PartialUserDto> getUsersPage(@ModelAttribute UserSearchParamsDto searchParamsDto,
                                                @ModelAttribute PageRequestDto pageRequestDto) {
        return service.getPage(searchParamsDto, pageRequestDto);
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user.
     * @return the {@link PartialUserDto} object representing the user with the specified ID.
     */
    @GetMapping(value = {"/{id}", "/empId/{id}"})
    public PartialUserDto getUser(@PathVariable long id) {
        return service.getUser(id);
    }

    /**
     * Retrieves a list of users based on a search query.
     *
     * @param q the search query string to filter users by. This can match against various user attributes.
     * @return a list of {@link PartialUserDto} objects that match the search query.
     */
    @GetMapping(value = {"/search", "/empId/search"})
    public List<PartialUserDto> getUsersSearch(@RequestParam String q) {
        return service.getUsersSearch(q);
    }
}
