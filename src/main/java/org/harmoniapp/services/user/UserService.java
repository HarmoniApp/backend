package org.harmoniapp.services.user;

import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.contracts.user.UserSearchParamsDto;

import java.util.List;

/**
 * Service interface for managing users.
 */
public interface UserService extends FindUser {

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return the user with the specified ID
     */
    UserDto get(long id);

    /**
     * Retrieves a paginated list of users based on search parameters.
     *
     * @param searchParamsDto the search parameters
     * @param pageRequestDto  the pagination information
     * @return a paginated list of users
     */
    PageDto<UserDto> getPage(UserSearchParamsDto searchParamsDto, PageRequestDto pageRequestDto);

    /**
     * Searches for users based on a query string.
     *
     * @param q the query string
     * @return a list of users matching the query
     */
    List<UserDto> getUsersSearch(String q);

    /**
     * Creates a new user.
     *
     * @param userDto the user data transfer object
     * @return the created user
     */
    UserDto create(UserDto userDto);

    /**
     * Updates an existing user.
     *
     * @param id      the ID of the user to update
     * @param userDto the user data transfer object
     * @return the updated user
     */
    UserDto update(long id, UserDto userDto);

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    void delete(long id);
}
