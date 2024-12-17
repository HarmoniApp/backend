package org.harmoniapp.services.user;

import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.PartialUserDto;
import org.harmoniapp.contracts.user.UserSearchParamsDto;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Service interface for partial user operations.
 */
public interface PartialUserService {
    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return the user details
     */
    PartialUserDto getUser(long id);

    /**
     * Retrieves a paginated list of users based on search parameters.
     *
     * @param searchParamsDto the search parameters
     * @param pageRequestDto  the pagination details
     * @return a paginated list of users
     */
    PageDto<PartialUserDto> getPage(@Nullable UserSearchParamsDto searchParamsDto, PageRequestDto pageRequestDto);

    /**
     * Searches for users based on a query string.
     *
     * @param q the query string
     * @return a list of users matching the query
     */
    List<PartialUserDto> getUsersSearch(String q);
}
