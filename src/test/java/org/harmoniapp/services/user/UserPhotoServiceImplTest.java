package org.harmoniapp.services.user;

import org.harmoniapp.contracts.user.PhotoDto;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserPhotoServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserPhotoServiceImpl userPhotoService;

    @BeforeEach
    public void setUp() {
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
    }

    @Test
    void getUserPhotoUserExistsWithPhotoTest() throws Exception {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setPhoto("userPhoto.jpg");

        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));
        Path photoPath = Paths.get("src/test/resources/static/userPhoto/test_photo1.jpg");
        Files.createDirectories(photoPath.getParent());
        Files.write(photoPath, "dummy content".getBytes());

        PhotoDto photoDto = userPhotoService.getUserPhoto(userId);

        assertNotNull(photoDto);
        assertEquals(MediaType.IMAGE_JPEG, photoDto.contentType());
        assertTrue(photoDto.photo().getInputStream().available() > 0);

        Files.deleteIfExists(photoPath);
    }

    @Test
    void getUserPhotoUserExistsWithoutPhotoTest() throws Exception {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setPhoto(null);

        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));
        Path defaultPhotoPath = Paths.get("src/test/resources/static/userPhoto/test_photo2.jpg");
        Files.createDirectories(defaultPhotoPath.getParent());
        Files.write(defaultPhotoPath, "dummy content".getBytes());

        PhotoDto photoDto = userPhotoService.getUserPhoto(userId);

        assertNotNull(photoDto);
        assertEquals(MediaType.IMAGE_JPEG, photoDto.contentType());
        assertTrue(photoDto.photo().getInputStream().available() > 0);

        // Clean up
        Files.deleteIfExists(defaultPhotoPath);
    }

    @Test
    void getUserPhotoUserNotFoundTest() {
        long userId = 1L;
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userPhotoService.getUserPhoto(userId));
    }

    @Test
    void uploadPhotoTest() throws Exception {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setPhoto("oldPhoto.jpg");

        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));
        Path oldPhotoPath = Paths.get("src/test/resources/static/userPhoto/oldPhoto.jpg");
        Files.createDirectories(oldPhotoPath.getParent());
        Files.write(oldPhotoPath, "old content".getBytes());

        MultipartFile file = new MockMultipartFile("file", "newPhoto.jpg", MediaType.IMAGE_JPEG_VALUE, "new content".getBytes());
        Path photoPath = Paths.get("src/main/resources/static/userPhoto/1_newPhoto.jpg");

        UserDto userDto = userPhotoService.uploadPhoto(userId, file);

        assertNotNull(userDto);
        assertEquals("1_newPhoto.jpg", userDto.photo());
        assertTrue(Files.exists(photoPath));

        Files.deleteIfExists(photoPath);
    }

    @Test
    void setDefaultPhotoTest() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setPhoto("customPhoto.jpg");
        try {
            when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));
            Path customPhotoPath = Paths.get("src/main/resources/static/userPhoto/customPhoto.jpg");
            Files.createDirectories(customPhotoPath.getParent());
            Files.write(customPhotoPath, "custom content".getBytes());

            UserDto userDto = userPhotoService.setDefaultPhoto(userId);

            assertNotNull(userDto);
            assertEquals("default.jpg", userDto.photo());
            assertFalse(Files.exists(customPhotoPath));

            Files.deleteIfExists(customPhotoPath);
        } catch (IOException e) {
            fail("An error occurred while creating the custom photo file");
        }
    }
}
