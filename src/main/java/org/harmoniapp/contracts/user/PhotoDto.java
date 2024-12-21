package org.harmoniapp.contracts.user;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

/**
 * A Data Transfer Object (DTO) for representing a photo.
 *
 * @param photo the photo as an InputStreamResource
 * @param contentType the content type of the photo as MediaType
 */
public record PhotoDto(InputStreamResource photo, MediaType contentType) {
}
