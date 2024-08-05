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
public class UserController {
    private final UserService service;

    /**
     * Retrieves all users with pagination support.
     *
     * @param page The page number to retrieve, default is 0.
     * @return A list of UserDto objects for the specified page.
     */
    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "0") int page) {
        return service.getUsers(page);
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

    @GetMapping("/search")
    public List<UserDto> getUsersWithFilter(@RequestParam(name = "role", required = false) List<String> roles,
                                            @RequestParam(name = "contract", required = false) List<String> contracts,
                                            @RequestParam(name = "language", required = false) List<String> language,
                                            @RequestParam(name = "sortBy", required = false, defaultValue = "firstname") String  sortBy,
                                            @RequestParam(name = "order", required = false, defaultValue = "asc") String  order) {
        return service.getUsersWithFilter(roles, contracts, language, sortBy, order);
    }

    @GetMapping("/search-nav") // temporary path
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
