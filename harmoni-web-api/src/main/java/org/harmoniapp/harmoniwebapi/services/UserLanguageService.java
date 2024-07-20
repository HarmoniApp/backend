package org.harmoniapp.harmoniwebapi.services;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.UserLanguageDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing user languages.
 * Provides methods to retrieve user information and their associated languages.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class UserLanguageService {
    private final RepositoryCollector repositoryCollector;
    private final int page_size = 20;

    /**
     * Retrieves a UserLanguageDto for the user with the specified ID.
     *
     * @param id the ID of the user to retrieve
     * @return a UserLanguageDto containing user firstname, surname and their languages
     * @throws IllegalArgumentException if the user with the specified ID does not exist
     */
    public UserLanguageDto getUser(long id) {
        var userOptional = repositoryCollector.getUsers().findById(id);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException();
        }

        var user = userOptional.get();

        return new UserLanguageDto(user.getId(), user.getFirstname(), user.getSurname(),
                user.getLanguages().stream().map(UserLanguage::getLanguage).collect(Collectors.toSet()));
    }

    /**
     * Retrieves a paginated list of UserLanguageDto.
     *
     * @param page the page number to retrieve
     * @return a list of UserLanguageDto containing user firstname, surname and their languages
     */
    public List<UserLanguageDto> getUsersPage(int page) {
        List<User> users = repositoryCollector.getUsers().findAll();
        List<List<User>> pagedUsers = Lists.partition(users, page_size);

        return pagedUsers.get(page).stream()
                .map(p -> new UserLanguageDto(
                        p.getId(),
                        p.getFirstname(),
                        p.getSurname(),
                        p.getLanguages().stream().map(UserLanguage::getLanguage).collect(Collectors.toSet())
                )).toList();
    }
}
