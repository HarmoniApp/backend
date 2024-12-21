package org.harmoniapp.contracts;

import org.springframework.web.bind.annotation.RequestParam;

/**
 * Data Transfer Object for pagination and sorting parameters.
 *
 * @param pageNumber the page number to retrieve, defaults to 1 if not provided
 * @param pageSize the number of items per page, defaults to 10 if not provided
 * @param sortBy the field to sort by, optional
 * @param order the sort order, either 'asc' or 'desc', defaults to 'asc' if not provided
 */
public record PageRequestDto(@RequestParam(required = false, defaultValue = "1") Integer pageNumber,
                             @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                             @RequestParam(required = false) String sortBy,
                             @RequestParam(required = false, defaultValue = "asc") String order) {
}
