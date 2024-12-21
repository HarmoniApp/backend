package org.harmoniapp.controllers.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.services.user.UserPhotoService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserPhotoController {
    private final UserPhotoService service;

    /**
     * Uploads a photo for a specific user.
     *
     * @param id   The ID of the user for whom the photo is being uploaded.
     * @param file The MultipartFile representing the uploaded photo. Must be either JPG or PNG format.
     * @return The updated UserDto object with the new photo path.
     */
    @PatchMapping("/{id}/uploadPhoto")
    public UserDto uploadPhoto(@PathVariable long id, @RequestParam("file") MultipartFile file) {
        return service.uploadPhoto(id, file);
    }

    /**
     * Sets the user's photo to the default photo.
     *
     * @param id The ID of the user whose photo is to be set to default.
     * @return The updated UserDto object with the default photo.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     * @throws RuntimeException         if there is an error deleting the old photo file.
     */
    @PatchMapping("/{id}/defaultPhoto")
    public UserDto setDefaultPhoto(@PathVariable long id) {
        return service.setDefaultPhoto(id);
    }

    /**
     * Retrieves the photo of a specific user by their ID.
     *
     * @param id The ID of the user whose photo is to be retrieved.
     * @return A ResponseEntity containing the InputStreamResource of the user's photo.
     */
    @GetMapping("/{id}/photo")
    public ResponseEntity<InputStreamResource> getUserPhoto(@PathVariable long id) {
        return service.getUserPhoto(id);
    }
}
