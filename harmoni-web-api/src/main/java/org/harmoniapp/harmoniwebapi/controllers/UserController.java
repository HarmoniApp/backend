package org.harmoniapp.harmoniwebapi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.PageDto;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.harmoniapp.harmoniwebapi.contracts.UserNewPassword;
import org.harmoniapp.harmoniwebapi.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * @param roles      Optional list of role IDs to filter users by roles.
     * @param contracts  Optional list of contract IDs to filter users by contracts.
     * @param language   Optional list of language IDs to filter users by languages.
     * @param pageNumber Optional page number to retrieve (optional, default is 1).
     * @param pageSize   Optional number of items per page (optional, default is 10).
     * @param sortBy     Optional field name by which the results should be sorted. Defaults to "firstname" if not provided.
     * @param order      Optional sort order for the results. Can be "asc" for ascending or "desc" for descending. Defaults to "asc" if not provided.
     * @return A PageDto containing a list of UserDto objects matching the specified criteria, sorted as requested.
     */
    @GetMapping
    public PageDto<UserDto> getAllUsers(@RequestParam(name = "role", required = false) List<Long> roles,
                                        @RequestParam(name = "contract", required = false) List<Long> contracts,
                                        @RequestParam(name = "language", required = false) List<Long> language,
                                        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                        @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                                        @RequestParam(name = "sortBy", required = false, defaultValue = "firstname") String sortBy,
                                        @RequestParam(name = "order", required = false, defaultValue = "asc") String order) {
        return service.getUsers(roles, contracts, language, pageNumber, pageSize, sortBy, order);
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

    @GetMapping("{id}/availableAbsenceDays")
    public int getAvailableAbsenceDays(@PathVariable Long id) {
        return service.getUserAvailableAbsenceDays(id);
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
        return service.add(userDto);
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
     * Uploads a photo for a specific user.
     *
     * @param id   The ID of the user for whom the photo is being uploaded.
     * @param file The MultipartFile representing the uploaded photo. Must be either JPG or PNG format.
     * @return The updated UserDto object with the new photo path.
     */
    @PatchMapping("/{id}/upload-photo")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto uploadPhoto(@PathVariable long id, @RequestParam("file") MultipartFile file) {
        return service.uploadPhoto(id, file);
    }

    /**
     * Sets the user's photo to the default photo.
     *
     * @param id The ID of the user whose photo is to be set to default.
     * @return The updated UserDto object with the default photo.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     * @throws RuntimeException         if there is an error deleting the old photo file.
     */
    @PatchMapping("/{id}/default-photo")
    public UserDto setDefaultPhoto(@PathVariable long id) {
        return service.setDefaultPhoto(id);
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

    @PatchMapping("/{id}/changePassword")
    public String changePassword(@PathVariable long id, @RequestBody UserNewPassword password) {
        return service.changePassword(id, password);
    }

    @PatchMapping("/{id}/generatePassword")
    public String generateNewPassword(@PathVariable long id) {
        return service.generateNewPassword(id);
    }
}
