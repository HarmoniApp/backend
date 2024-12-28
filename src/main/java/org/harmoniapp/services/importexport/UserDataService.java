package org.harmoniapp.services.importexport;

import org.harmoniapp.contracts.user.UserDto;

import java.util.List;

/**
 * Service interface for managing user data operations.
 */
public interface UserDataService {

    /**
     * Retrieves a list of all active users.
     *
     * @return a list of UserDto objects representing all active users
     */
    List<UserDto> getAllUsers();

    /**
     * Retrieves the roles of a given user as a comma-separated string.
     *
     * @param user the UserDto object representing the user
     * @return a string containing the roles of the user, separated by commas
     */
    String getRoles(UserDto user);

    /**
     * Retrieves the languages of a given user as a comma-separated string.
     *
     * @param user the UserDto object representing the user
     * @return a string containing the languages of the user, separated by commas
     */
    String getLanguages(UserDto user);

    /**
     * Retrieves the employee ID of the supervisor of a given user.
     *
     * @param user the UserDto object representing the user
     * @return a string containing the employee ID of the supervisor, or an empty string if the supervisor is not found
     */
    String getSupervisorEmployeeId(UserDto user);
}
