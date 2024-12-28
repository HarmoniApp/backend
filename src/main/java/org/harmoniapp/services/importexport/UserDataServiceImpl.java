package org.harmoniapp.services.importexport;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for user data operations.
 */
@Service
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a list of all active users, sorted by surname and firstname.
     *
     * @return a list of UserDto objects representing all active users
     */
    public List<UserDto> getAllUsers() {
        return repositoryCollector.getUsers()
                .findByIsActiveTrue(Sort.by("surname", "firstname"))
                .stream()
                .map(UserDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves the roles of a given user as a comma-separated string.
     *
     * @param user the UserDto object representing the user
     * @return a string containing the roles of the user, separated by commas
     */
    public String getRoles(UserDto user) {
        return user.roles().stream()
                .map(RoleDto::name)
                .collect(Collectors.joining(", "));
    }

    /**
     * Retrieves the languages of a given user as a comma-separated string.
     *
     * @param user the UserDto object representing the user
     * @return a string containing the languages of the user, separated by commas
     */
    public String getLanguages(UserDto user) {
        return user.languages().stream()
                .map(LanguageDto::name)
                .collect(Collectors.joining(", "));
    }

    /**
     * Retrieves the employee ID of the supervisor of a given user.
     *
     * @param user the UserDto object representing the user
     * @return a string containing the employee ID of the supervisor, or an empty string if the supervisor is not found
     */
    public String getSupervisorEmployeeId(UserDto user) {
        if (user.supervisorId() != null) {
            return repositoryCollector.getUsers()
                    .findById(user.supervisorId())
                    .map(User::getEmployeeId)
                    .orElse("");
        }
        return "";
    }
}
