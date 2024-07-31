package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.PartialUserDto;
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
public class PartialUserService {
    private final RepositoryCollector repositoryCollector;
    private final int page_size = 20;

    /**
     * Retrieves a UserLanguageDto for the user with the specified ID.
     *
     * @param id the ID of the user to retrieve
     * @return a UserLanguageDto containing user firstname, surname and their languages
     * @throws IllegalArgumentException if the user with the specified ID does not exist
     */
    public PartialUserDto getUser(long id) {
        User user = repositoryCollector.getUsers().findById(id).orElseThrow(IllegalArgumentException::new);

        return PartialUserDto.fromEntity(user);
    }

    /**
     * Retrieves a paginated list of UserLanguageDto.
     *
     * @param page the page number to retrieve
     * @return a list of UserLanguageDto containing user firstname, surname and their languages
     */
    public List<PartialUserDto> getUsersPage(int page) {
        List<User> users = repositoryCollector.getUsers().findAll();
//        List<List<User>> pagedUsers = Lists.partition(users, page_size);

        return users.stream()
                .map(PartialUserDto::fromEntity)
                .toList();
    }
}
