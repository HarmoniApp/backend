package org.harmoniapp.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.user.UserNewPasswordDto;
import org.harmoniapp.services.auth.LoginService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for {@link UserPasswordController} class
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class UserPasswordControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;
    private static String jwtUser;
    private static ObjectMapper mapper;

    @Autowired
    public UserPasswordControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public static void setUp(@Autowired LoginService loginService) {
        // Login as an admin to get a JWT token
        var credentialsAdmin = new LoginRequestDto("jan.kowalski@example.com", "StrongPassword!2137");
        jwtAdmin = loginService.login(credentialsAdmin).jwtToken();

        // Login as a user to get a JWT token
        var credentialsUser = new LoginRequestDto("piotr.wisniewski@example.com", "StrongPassword!2137");
        jwtUser = loginService.login(credentialsUser).jwtToken();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void changePasswordNotOwnerTest() throws Exception {
        var newPassword = new UserNewPasswordDto("StrongPassword!2137");
        var json = mapper.writeValueAsString(newPassword);

        mockMvc.perform(MockMvcRequestBuilders.patch("/user/1/changePassword")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void changePasswordAsOwnerTest() throws Exception {
        var newPassword = new UserNewPasswordDto("StrongPassword!2137");
        var json = mapper.writeValueAsString(newPassword);

        mockMvc.perform(MockMvcRequestBuilders.patch("/user/3/changePassword")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hasło zmienione pomyślnie"));
    }

    @Test
    public void changePasswordInvalidInputTest() throws Exception {
        var newPassword = new UserNewPasswordDto("");
        var json = mapper.writeValueAsString(newPassword);

        mockMvc.perform(MockMvcRequestBuilders.patch("/user/3/changePassword")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void changePasswordWeakPasswordTest() throws Exception {
        var newPassword = new UserNewPasswordDto("test");
        var json = mapper.writeValueAsString(newPassword);

        mockMvc.perform(MockMvcRequestBuilders.patch("/user/3/changePassword")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void generateNewPasswordAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/user/3/generatePassword")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void generateNewPasswordAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/user/3/generatePassword")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
