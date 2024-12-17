package org.harmoniapp.services.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Service implementation for managing user photos.
 */
@Service
@RequiredArgsConstructor
public class UserPhotoServiceImpl implements UserPhotoService {
    private final RepositoryCollector repositoryCollector;
    private final String photoDirPath = "src/main/resources/static/userPhoto/";

    /**
     * Retrieves the photo of a specific user by their ID.
     *
     * @param id The ID of the user whose photo is to be retrieved.
     * @return A ResponseEntity containing the InputStreamResource of the user's photo.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     * @throws RuntimeException         if there is an error reading the photo file.
     */
    public ResponseEntity<InputStreamResource> getUserPhoto(long id) {
        User user = getUserById(id);
        String photo = user.getPhoto();
        MediaType contentType = determineContentType(photo);

        Path photoPath = getPhotoPath(photo);
        if (!Files.exists(photoPath)) {
            photoPath = getPhotoPath("default.jpg");
        }

        try {
            InputStream in = new FileInputStream(photoPath.toString());
            return ResponseEntity.ok().contentType(contentType).body(new InputStreamResource(in));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read photo file", e);
        }
    }

    /**
     * Uploads a photo for a specific user and saves it to the disk.
     *
     * @param id   The ID of the user to associate the photo with.
     * @param file The photo file to be uploaded (must be in JPG or PNG format).
     * @return The updated UserDto object after saving the photo information.
     * @throws IllegalArgumentException if the user is not found or the file format is not supported.
     * @throws RuntimeException         if there is an error saving the file.
     */
    public UserDto uploadPhoto(long id, MultipartFile file) {
        validateFileFormat(file);
        User user = getUserById(id);
        String uploadDirectory = createUploadDirectory();

        try {
            String oldPhoto = user.getPhoto();
            String newFileName = saveNewPhoto(file, user, uploadDirectory);
            user.setPhoto(newFileName);
            repositoryCollector.getUsers().save(user);
            deleteOldPhoto(uploadDirectory, oldPhoto);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        return UserDto.fromEntity(user);
    }

    /**
     * Sets the user's photo to the default photo.
     *
     * @param id The ID of the user whose photo is to be set to default.
     * @return The updated UserDto object with the default photo.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     * @throws RuntimeException         if there is an error deleting the old photo file.
     */
    public UserDto setDefaultPhoto(long id) {
        User user = getUserById(id);

        if (isDefaultPhoto(user.getPhoto())) {
            return UserDto.fromEntity(user);
        }

        String uploadDirectory = createUploadDirectory();
        deleteOldPhoto(uploadDirectory, user.getPhoto());

        user.setPhoto("default.jpg");
        repositoryCollector.getUsers().save(user);

        return UserDto.fromEntity(user);
    }

    /**
     * Retrieves the user entity by its ID if it is active.
     *
     * @param id The ID of the user to retrieve.
     * @return The user entity with the specified ID if it is active.
     * @throws IllegalArgumentException if the user is not found.
     */
    private User getUserById(long id) {
        return repositoryCollector.getUsers().findByIdAndIsActive(id, true)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Determines the content type of the photo based on its file extension.
     *
     * @param photo The name of the photo file.
     * @return The MediaType corresponding to the photo's file extension.
     */
    private MediaType determineContentType(String photo) {
        return photo.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
    }

    /**
     * Constructs the file path for the given photo name.
     *
     * @param photo The name of the photo file.
     * @return The Path object representing the file path of the photo.
     */
    private Path getPhotoPath(String photo) {
        String uploadDirectory = new File(photoDirPath).getAbsolutePath();
        return Paths.get(uploadDirectory, photo);
    }

    /**
     * Validates the format of the uploaded file.
     *
     * @param file The MultipartFile to be validated.
     * @throws IllegalArgumentException if the file format is not JPG or PNG.
     */
    private void validateFileFormat(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                !(originalFilename.endsWith(".jpg") || originalFilename.endsWith(".png") || originalFilename.endsWith(".jpeg"))) {
            throw new IllegalArgumentException("File must be a JPG or PNG image");
        }
    }

    /**
     * Creates the upload directory if it does not exist.
     *
     * @return The absolute path of the upload directory.
     */
    private String createUploadDirectory() {
        String uploadDirectory = new File(photoDirPath).getAbsolutePath();
        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return uploadDirectory;
    }

    /**
     * Saves a new photo for the user to the specified upload directory.
     *
     * @param file            The MultipartFile containing the photo to be saved.
     * @param user            The user for whom the photo is being saved.
     * @param uploadDirectory The directory where the photo will be saved.
     * @return The name of the saved photo file.
     * @throws IOException if an I/O error occurs while saving the photo.
     */
    private String saveNewPhoto(MultipartFile file, User user, String uploadDirectory) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String newFileName = user.getId() + "_" + originalFilename;
        Path path = Paths.get(uploadDirectory, newFileName);
        Files.write(path, file.getBytes());
        return newFileName;
    }

    /**
     * Deletes the old photo file from the disk if it is not a default photo.
     *
     * @param uploadDirectory The directory where the photo is stored.
     * @param oldPhoto        The name of the old photo file to be deleted.
     * @throws RuntimeException if there is an error deleting the old photo file.
     */
    private void deleteOldPhoto(String uploadDirectory, String oldPhoto) {
        try {
            Path oldPhotoPath = Paths.get(uploadDirectory, oldPhoto);
            if (!isDefaultPhoto(oldPhoto) && Files.exists(oldPhotoPath)) {
                Files.delete(oldPhotoPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete old photo", e);
        }
    }

    /**
     * Checks if the given photo is a default photo.
     *
     * @param photo The name of the photo file.
     * @return true if the photo is a default photo, false otherwise.
     */
    private boolean isDefaultPhoto(String photo) {
        List<String> defaultPhotos = List.of("default.jpg", "man.jpg", "woman.jpg");
        return defaultPhotos.contains(photo);
    }
}
