package org.harmoniapp.services.user;

import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.UserSearchParamsDto;
import org.harmoniapp.entities.user.User;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for searching users.
 */
public interface UserSearchService {

    /**
     * Searches for users based on a query string.
     *
     * @param q the query string to search for
     * @return a list of users matching the query
     */
    List<User> searchUsers(String q);

    /**
     * Finds a paginated list of users based on page request and search parameters.
     *
     * @param pageRequestDto  the page request details
     * @param searchParamsDto the search parameters
     * @return a paginated list of users
     */
    Page<User> findUsersPage(PageRequestDto pageRequestDto, UserSearchParamsDto searchParamsDto);

    /**
     * Finds a paginated list of users based on page request.
     *
     * @param pageRequestDto the page request details
     * @return a paginated list of users
     */
    Page<User> findUsersPage(PageRequestDto pageRequestDto);
}
