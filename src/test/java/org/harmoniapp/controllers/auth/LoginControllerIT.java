package org.harmoniapp.controllers.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link LoginController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class LoginControllerIT {

    private final MockMvc mockMvc;

    @Autowired
    public LoginControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void loginCorrectCredentialsTest() throws Exception {
        String body = "{\"username\": \"jan.kowalski@example.com\", \"password\": \"StrongPassword!2137\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType("application/json")
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("Authorization"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwtToken").exists());
    }

    @Test
    public void loginWithoutCredentialsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void loginBadUsernameTest() throws Exception {
        String body = "{\"username\": \"a@example.com\", \"password\": \"asd\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType("application/json")
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

    @Test
    public void loginBadCredentialsTest() throws Exception {
        String body = "{\"username\": \"jan.kowalski@example.com\", \"password\": \"asd\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType("application/json")
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void loginWithOTPTest() throws Exception {
        String body = "{\"username\": \"piotr.kowal4@example.com\", \"password\": \"ashvgd!@3sad\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType("application/json")
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("Authorization"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwtToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.path").exists());
    }

    @Test
    public void loginAsInactiveUserTest() throws Exception {
        String body = "{\"username\": \"adam.piotrowski5@example.com\", \"password\": \"StrongPassword!2137\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType("application/json")
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
