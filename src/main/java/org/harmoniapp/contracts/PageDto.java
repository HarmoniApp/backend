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

    public static <T, U> PageDto<T> mapPage(Page<U> surcePage, Function<U, T> mapper) {
        return new PageDto<>(surcePage.getContent().stream().map(mapper).toList(),
                surcePage.getSize(),
                surcePage.getNumber() + 1,
                surcePage.getTotalPages());
    }
}
