package org.harmoniapp.contracts;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Represents a generic DTO (Data Transfer Object) for paginated data.
 *
 * @param <T> the type of elements in the content list
 */
public record PageDto<T>(List<T> content, int pageSize, int pageNumber, int totalPages) {

    public static <T, U> PageDto<T> mapPage(Page<U> sourcePage, Function<U, T> mapper) {
        return new PageDto<>(sourcePage.getContent().stream().map(mapper).toList(),
                sourcePage.getSize(),
                sourcePage.getNumber() + 1,
                sourcePage.getTotalPages());
    }
}
