package org.harmoniapp.controllers.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Integration tests for the {@link UserScheduleController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class UserScheduleControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;
    private static String jwtUser;

    @Autowired
    public UserScheduleControllerIT(MockMvc mockMvc) {
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
    }


    @Test
    public void getUserWeeklyScheduleAsAdminTest() throws Exception {
        Long userId = 3L;
        LocalDateTime start = LocalDateTime.of(2025, 1, 6, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 13, 0, 0, 0);
        String url = "/calendar/user/%s/week?&startDate=%s&endDate=%s&published=%s"
                .formatted(userId, start, end, false);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shifts").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.absences").isArray());
    }

    @Test
    public void getUserWeeklyScheduleAsOwnerTest() throws Exception {
        Long userId = 3L;
        LocalDateTime start = LocalDateTime.of(2025, 1, 6, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 13, 0, 0, 0);
        String url = "/calendar/user/%s/week?&startDate=%s&endDate=%s&published=%s"
                .formatted(userId, start, end, true);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwtUser))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shifts").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.absences").isArray());
    }

    @Test
    public void getUserWeeklyScheduleAsNotOwnerTest() throws Exception {
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.of(2025, 1, 6, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 13, 0, 0, 0);
        String url = "/calendar/user/%s/week?&startDate=%s&endDate=%s&published=%s"
                .formatted(userId, start, end, false);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwtUser))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getUserWeeklyScheduleInvalidUserIdTest() throws Exception {
        Long userId = 0L;
        LocalDateTime start = LocalDateTime.of(2025, 1, 6, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 13, 0, 0, 0);
        String url = "/calendar/user/%s/week?&startDate=%s&endDate=%s&published=%s"
                .formatted(userId, start, end, false);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getUserWeeklyScheduleWithoutQueryParamsTest() throws Exception {
        Long userId = 1L;
        String url = "/calendar/user/%s/week".formatted(userId);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
