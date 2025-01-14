package org.harmoniapp.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.profile.AddressDto;
import org.harmoniapp.contracts.profile.ContractTypeDto;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.services.auth.LoginService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * Test class for {@link UserPhotoController} class
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class UserPhotoControllerIT {

    private final MockMvc mockMvc;
    private static String jwtUser;
    private static ObjectMapper mapper;

    @Autowired
    public UserPhotoControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public static void setUp(@Autowired LoginService loginService) {
        // Login as a user to get a JWT token
        var credentialsUser = new LoginRequestDto("piotr.wisniewski@example.com", "StrongPassword!2137");
        jwtUser = loginService.login(credentialsUser).jwtToken();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void getUserPhotoTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/3/photo")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    public void getUserPhotoInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/0/photo")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void setDefaultPhotoAsOwnerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/user/3/defaultPhoto")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.photo").value("default.jpg"));
    }

    @Test
    public void setDefaultPhotoAsNotOwnerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/user/1/defaultPhoto")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void uploadPhotoAsOwnerTest() throws Exception {
        File photo = new File("src/test/resources/testFiles/test_photo.jpg");
        InputStream inputStream = new FileInputStream(photo);
        MockMultipartFile mockFile = new MockMultipartFile("file", "test_photo.jpg", "image/jpeg", inputStream);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/user/3/uploadPhoto")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.photo").value("3_test_photo.jpg"));
    }

    @Test
    public void uploadPhotoAsNotOwnerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/user/1/uploadPhoto")
                        .file("file", "test.jpg".getBytes())
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void uploadPhotoInvalidFileFormatTest() throws Exception {
        MockMultipartFile largeFile = new MockMultipartFile(
                "file", "largeFile.exe", "application/octet-stream", "dummy content".getBytes()); // Large content
        mockMvc.perform(MockMvcRequestBuilders.multipart("/user/3/uploadPhoto")
                        .file(largeFile)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .header("Authorization", "Bearer " + jwtUser))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
