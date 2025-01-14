package org.harmoniapp.controllers.notification;

import org.harmoniapp.contracts.auth.LoginRequestDto;
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
 * Integration tests for the {@link NotificationController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class NotificationControllerIT {

    private final MockMvc mockMvc;
    private static String jwt;

    @Autowired
    public NotificationControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public static void setUp(@Autowired LoginService loginService) {
        // Login as a user to get a JWT token
        var credentials = new LoginRequestDto("jan.kowalski@example.com", "StrongPassword!2137");
        jwt = loginService.login(credentials).jwtToken();
    }

    @Test
    public void getAllNotificationsByUserIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/notification/user/1")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void getAllNotificationsByInvalidUserIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/notification/user/2/unread")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getAllUnreadNotificationsByUserIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/notification/user/1/unread")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].read").value(false));
    }

    @Test
    public void getAllUnreadNotificationsByInvalidUserIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/notification/user/2/unread")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void markAllAsReadByUserIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/notification/user/1/read")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].read").value(true));
    }

    @Test
    public void markAllAsReadByInvalidUserIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/notification/user/2/read")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void deleteNotificationByIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/notification/1")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteNotificationOfAnotherUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/notification/22")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
