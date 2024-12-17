package org.harmoniapp.services.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.PartialUserDto;
import org.harmoniapp.contracts.user.UserSearchParamsDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the PartialUserService interface.
 * This service provides methods to manage and retrieve partial user information.
 */
@Service
@RequiredArgsConstructor
public class PartialUserServiceImpl implements PartialUserService {
    private final RepositoryCollector repositoryCollector;
    private final UserSearchService userSearchService;

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return the partial user data transfer object
     */
    public PartialUserDto getUser(long id) {
        User user = getUserById(id);
        return PartialUserDto.fromEntity(user);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return the user entity
     */
    private User getUserById(long id) {
        return repositoryCollector.getUsers().findByIdAndIsActive(id, true)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Retrieves a paginated list of users based on search parameters.
     *
     * @param searchParamsDto the search parameters
     * @param pageRequestDto  the page request details
     * @return a page data transfer object containing partial user data transfer objects
     */
    public PageDto<PartialUserDto> getPage(@Nullable UserSearchParamsDto searchParamsDto, PageRequestDto pageRequestDto) {
        Page<User> users = userSearchService.findUsersPage(pageRequestDto, searchParamsDto);
        return PageDto.mapPage(users, PartialUserDto::fromEntity);
    }

    /**
     * Searches for users based on a query string.
     *
     * @param q the query string
     * @return a list of partial user data transfer objects
     */
    public List<PartialUserDto> getUsersSearch(String q) {
        return userSearchService.searchUsers(q)
                .stream()
                .map(PartialUserDto::fromEntity)
                .toList();
    }
}
