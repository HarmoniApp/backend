package org.harmoniapp.contracts.user;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Data Transfer Object for user search parameters.
 *
 * @param roles     List of role IDs to filter users by roles.
 * @param contracts List of contract IDs to filter users by contracts.
 * @param language  List of language IDs to filter users by language.
 */
public record UserSearchParamsDto(
        @RequestParam(required = false) List<Long> roles,
        @RequestParam(required = false) List<Long> contracts,
        @RequestParam(required = false) List<Long> language){
}
