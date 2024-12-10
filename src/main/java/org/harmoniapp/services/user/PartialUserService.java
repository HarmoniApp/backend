package org.harmoniapp.services.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.user.PartialUserDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing user languages.
 * Provides methods to retrieve user information and their associated languages.
 */
@Service
@RequiredArgsConstructor
public class PartialUserService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user.
     * @return the {@link PartialUserDto} object representing the user with the specified ID.
     * @throws IllegalArgumentException if no user with the specified ID is found.
     */
    public PartialUserDto getUser(long id) {
        User user = repositoryCollector.getUsers().findByIdAndIsActive(id, true).orElseThrow(IllegalArgumentException::new);

        return PartialUserDto.fromEntity(user);
    }

    /**
     * /**
     * Retrieves a paginated list of users based on the specified filtering and sorting criteria.
     *
     * @param roles      an optional list of role IDs to filter the users by roles. If not specified, no role-based filtering is applied.
     * @param contracts  an optional list of contract IDs to filter the users by contracts. If not specified, no contract-based filtering is applied.
     * @param languages  an optional list of language IDs to filter the users by languages. If not specified, no language-based filtering is applied.
     * @param pageNumber the page number to retrieve.
     * @param pageSize   the number of users per page.
     * @param sortBy     the field by which to sort the results. Default is "firstname".
     * @param order      the order of sorting, either "asc" for ascending or "desc" for descending. Default is "asc".
     * @return a {@link PageDto} containing a list of {@link PartialUserDto} objects that match the specified criteria.
     */
    public PageDto<PartialUserDto> getUsers(List<Long> roles, List<Long> contracts, List<Long> languages, int pageNumber, int pageSize, String sortBy, String order) {
        pageNumber = (pageNumber < 1) ? 0 : pageNumber-1;
        pageSize = (pageSize < 1) ? 10 : pageSize;

        Sort.Direction sortDirection;
        if (order == null || order.isEmpty() || order.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        } else {
            sortDirection = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, sortBy));

        Page<User> users;
        if ((roles == null || roles.isEmpty())
                && (contracts == null || contracts.isEmpty())
                && (languages == null || languages.isEmpty())) {
            users = repositoryCollector.getUsers().findAllByIsActive(true, pageable);
        } else {
            users = repositoryCollector.getUsers().findAllByContractAndRoleAndLanguageAndIsActive(contracts, roles, languages, true, pageable);
        }

        return new PageDto<PartialUserDto>(
                users.stream().map(PartialUserDto::fromEntity).toList(),
                users.getSize(),
                users.getNumber()+1,
                users.getTotalPages());
    }

    /**
     * Searches for users based on a query string. The query string is split into individual words
     * and used to match against user attributes. If the query string is empty or null, an exception is thrown.
     *
     * @param q the search query string to filter users by. This can match against various user attributes.
     * @return a list of {@link PartialUserDto} objects that match the search query.
     * @throws IllegalArgumentException if the query string is null or empty.
     */
    public List<PartialUserDto> getUsersSearch(String q) {
        if (q == null || q.isEmpty()) {
            throw new IllegalArgumentException();
        }

        q = q.toUpperCase();
        List<String> qSplit = List.of(q.split(" "));

        List<User> users;
        if (qSplit.size() > 1) {
            users = repositoryCollector.getUsers().findAllBySearchName(qSplit, true);
        } else {
            users = repositoryCollector.getUsers().FindAllBySearch(q, true);
        }

        return users.stream().map(PartialUserDto::fromEntity).collect(Collectors.toList());
    }
}
