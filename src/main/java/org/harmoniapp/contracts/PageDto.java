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

    /**
     * Maps a `Page` object to a `PageDto` object.
     *
     * @param <T> the type of elements in the target `PageDto`
     * @param <U> the type of elements in the source `Page`
     * @param sourcePage the source `Page` object to map from
     * @param mapper a function to convert elements from type `U` to type `T`
     * @return a `PageDto` object containing the mapped data
     */
    public static <T, U> PageDto<T> mapPage(Page<U> sourcePage, Function<U, T> mapper) {
        return new PageDto<>(sourcePage.getContent().stream().map(mapper).toList(),
                sourcePage.getSize(),
                sourcePage.getNumber() + 1,
                sourcePage.getTotalPages());
    }
}
