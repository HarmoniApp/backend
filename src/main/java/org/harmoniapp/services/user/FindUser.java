package org.harmoniapp.services.user;

import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Component;

/**
 * Interface for finding users.
 */
@Component
public interface FindUser {

    /**
     * Retrieves a user by their ID if they are active.
     *
     * @param id                  the ID of the user to retrieve
     * @param repositoryCollector the repository collector to use for retrieving the user
     * @return the user with the specified ID if they are active
     * @throws IllegalArgumentException if the user is not found
     */
    default User getUserById(long id, RepositoryCollector repositoryCollector) {
        return repositoryCollector.getUsers().findByIdAndIsActive(id, true)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
