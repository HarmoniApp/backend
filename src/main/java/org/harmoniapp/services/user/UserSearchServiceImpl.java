package org.harmoniapp.services.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.UserSearchParamsDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the UserSearchService interface.
 * This service provides methods for searching and retrieving users.
 */
@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Searches for users based on the provided query string.
     *
     * @param q The search query string.
     * @return A list of users that match the search criteria.
     * @throws IllegalArgumentException if the search query is null or empty.
     */
    @Override
    public List<User> searchUsers(String q) {
        validateSearchQuery(q);
        List<String> qSplit = splitQuery(q);
        return searchUsersInDb(qSplit);
    }

    /**
     * Validates the search query string.
     *
     * @param q The search query string to validate.
     * @throws IllegalArgumentException if the search query is null or empty.
     */
    private void validateSearchQuery(String q) {
        if (q == null || q.isEmpty()) {
            throw new IllegalArgumentException("Search query must not be null or empty");
        }
    }

    /**
     * Splits the search query string into a list of uppercase words.
     *
     * @param q The search query string to split.
     * @return A list of uppercase words from the search query string.
     */
    private List<String> splitQuery(String q) {
        return List.of(q.toUpperCase().split(" "));
    }

    /**
     * Searches for users in the database based on the split query string.
     *
     * @param qSplit The list of uppercase words from the search query string.
     * @return A list of users that match the search criteria.
     */
    private List<User> searchUsersInDb(List<String> qSplit) {
        if (qSplit.size() > 1) {
            return repositoryCollector.getUsers().findAllBySearchName(qSplit, true);
        } else {
            return repositoryCollector.getUsers().FindAllBySearch(qSplit.getFirst(), true);
        }
    }

    /**
     * Finds a paginated list of users.
     *
     * @param pageRequestDto The PageRequestDto containing pagination and sorting details.
     * @return A Page of User entities.
     */
    @Override
    public Page<User> findUsersPage(PageRequestDto pageRequestDto) {
        return findUsersPage(pageRequestDto, null);
    }

    /**
     * Finds a paginated list of users based on the provided PageRequestDto and optional search parameters.
     *
     * @param pageRequestDto  The PageRequestDto containing pagination and sorting details.
     * @param searchParamsDto The optional UserSearchParamsDto containing search parameters for filtering users.
     * @return A Page of User entities that match the search criteria.
     */
    @Override
    public Page<User> findUsersPage(PageRequestDto pageRequestDto, @Nullable UserSearchParamsDto searchParamsDto) {
        Pageable pageable = createPageable(pageRequestDto);
        return retrieveUsers(pageable, searchParamsDto);
    }

    /**
     * Creates a Pageable object based on the provided PageRequestDto.
     *
     * @param pageRequestDto The PageRequestDto containing pagination and sorting details.
     * @return A Pageable object with the specified page number, page size, and sorting direction.
     */
    private Pageable createPageable(PageRequestDto pageRequestDto) {
        int pageNumber = (pageRequestDto.pageNumber() < 1) ? 0 : pageRequestDto.pageNumber() - 1;
        int pageSize = (pageRequestDto.pageSize() < 1) ? 10 : pageRequestDto.pageSize();

        Sort.Direction sortDirection =
                (pageRequestDto.sortBy() == null || pageRequestDto.order().isEmpty() || pageRequestDto.sortBy().equalsIgnoreCase("asc"))
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;


        if (pageRequestDto.sortBy() != null) {
            return PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, pageRequestDto.sortBy()));
        } else {
            return PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, "firstname", "surname"));
        }
    }

    /**
     * Retrieves a paginated list of users based on search parameters.
     *
     * @param searchParamsDto The search parameters for filtering users.
     * @param pageable        The pagination and sorting details.
     * @return A Page of User entities that match the search criteria.
     */
    private Page<User> retrieveUsers(Pageable pageable, @Nullable UserSearchParamsDto searchParamsDto) {
        if (searchParamsDto == null ||
                ((searchParamsDto.roles() == null || searchParamsDto.roles().isEmpty())
                        && (searchParamsDto.contracts() == null || searchParamsDto.contracts().isEmpty())
                        && (searchParamsDto.language() == null || searchParamsDto.language().isEmpty()))) {
            return repositoryCollector.getUsers().findAllByIsActive(true, pageable);
        } else {
            return repositoryCollector.getUsers().findAllByContractAndRoleAndLanguageAndIsActive(
                    searchParamsDto.contracts(), searchParamsDto.roles(), searchParamsDto.language(), pageable);
        }
    }
}
