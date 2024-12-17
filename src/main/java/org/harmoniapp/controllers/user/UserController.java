package org.harmoniapp.controllers.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.contracts.user.UserSearchParamsDto;
import org.harmoniapp.services.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing users.
 * Provides endpoints to perform CRUD operations on users.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService service;

    /**
     * Retrieves a paginated list of UserDto objects based on specified request parameters.
     *
     * @param searchParams The search parameters to filter users.
     * @param pageRequest  The pagination and sorting information.
     * @return A PageDto containing a list of UserDto objects matching the specified criteria.
     */
    @GetMapping
    public PageDto<UserDto> getAllUsers(@ModelAttribute UserSearchParamsDto searchParams,
                                        @ModelAttribute PageRequestDto pageRequest) {
        return service.getPage(searchParams, pageRequest);
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserDto object corresponding to the specified ID.
     */
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        return service.get(id);
    }

    /**
     * Searches for users based on a query string and returns a list of UserDto objects.
     *
     * @param q The query string used to search for users. Must not be null or empty.
     * @return A list of UserDto objects that match the search criteria.
     */
    @GetMapping("/search")
    public List<UserDto> getUsersSearch(@RequestParam String q) {
        return service.getUsersSearch(q);
    }

    /**
     * Creates a new user.
     *
     * @param userDto The UserDto object representing the new user.
     * @return The created UserDto object.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return service.create(userDto);
    }

    /**
     * Updates an existing user.
     *
     * @param id      The ID of the user to update.
     * @param userDto The UserDto object containing the updated user data.
     * @return The updated UserDto object.
     */
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto updateUser(@PathVariable long id, @Valid @RequestBody UserDto userDto) {
        return service.update(id, userDto);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id) {
        service.delete(id);
    }
}
