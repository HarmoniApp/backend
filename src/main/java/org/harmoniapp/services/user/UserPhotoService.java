package org.harmoniapp.services.user;

import org.harmoniapp.contracts.user.UserDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for managing user photos.
 */
public interface UserPhotoService  {

    /**
     * Uploads a photo for the user with the given ID.
     *
     * @param id   the ID of the user
     * @param file the photo file to upload
     * @return the updated UserDto
     */
    UserDto uploadPhoto(long id, MultipartFile file);

    /**
     * Sets the default photo for the user with the given ID.
     *
     * @param id the ID of the user
     * @return the updated UserDto
     */
    UserDto setDefaultPhoto(long id);

    /**
     * Retrieves the photo of the user with the given ID.
     *
     * @param id the ID of the user
     * @return a ResponseEntity containing the InputStreamResource of the photo
     */
    ResponseEntity<InputStreamResource> getUserPhoto(long id);
}
