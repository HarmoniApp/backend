package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.harmoniapp.harmoniwebapi.services.UserService;
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
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService service;

    /**
     * Retrieves a list of UserDto objects based on specified request parameters.
     *
     * @param roles     Optional list of role IDs to filter users by roles.
     * @param contracts Optional list of contract IDs to filter users by contracts.
     * @param language  Optional list of language IDs to filter users by languages.
     * @param sortBy    Optional field name by which the results should be sorted. Defaults to "firstname" if not provided.
     * @param order     Optional sort order for the results. Can be "asc" for ascending or "desc" for descending. Defaults to "asc" if not provided.
     * @return A list of UserDto objects matching the specified criteria, sorted as requested.
     */
    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(name = "role", required = false) List<Long> roles,
                                     @RequestParam(name = "contract", required = false) List<Long> contracts,
                                     @RequestParam(name = "language", required = false) List<Long> language,
                                     @RequestParam(name = "sortBy", required = false, defaultValue = "firstname") String  sortBy,
                                     @RequestParam(name = "order", required = false, defaultValue = "asc") String  order) {
        return service.getUsers(roles, contracts, language, sortBy, order);
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserDto object corresponding to the specified ID.
     */
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        return service.getUser(id);
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
    public UserDto createUser(@RequestBody UserDto userDto) {
        return service.add(userDto);
    }

    /**
     * Updates an existing user.
     *
     * @param id      The ID of the user to update.
     * @param userDto The UserDto object containing the updated user data.
     * @return The updated UserDto object.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto updateUser(@PathVariable long id, @RequestBody UserDto userDto) {
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
