package org.harmoniapp.contracts;

import java.util.List;

/**
 * Represents a generic DTO (Data Transfer Object) for paginated data.
 *
 * @param <T> the type of elements in the content list
 */
public record PageDto<T>(List<T> content, int pageSize, int pageNumber, int totalPages) {
}
