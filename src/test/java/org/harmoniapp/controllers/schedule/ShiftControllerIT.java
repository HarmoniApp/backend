package org.harmoniapp.controllers.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.schedule.ShiftDto;
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

import java.time.LocalDateTime;

/**
 * Integration tests for the {@link ShiftController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ShiftControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;
    private static String jwtUser;
    private static ObjectMapper mapper;

    @Autowired
    public ShiftControllerIT(MockMvc mockMvc) {
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
    public void getShiftAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/shift/1")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void getShiftAsOwnerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/shift/2")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2));
    }

    @Test
    public void getShiftAsNotOwnerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/shift/1")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getShiftInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/shift/0")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getShiftsByDateRangeAndUserIdAsAdminTest() throws Exception {
        String url = "/shift/range?start=%s&end=%s&user_id=%d".formatted("2025-01-12T00:00:00", "2025-01-22T23:59:59", 3);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").value(3));
    }

    @Test
    public void getShiftsByDateRangeAndUserIdAsOwnerTest() throws Exception {
        String url = "/shift/range?start=%s&end=%s&user_id=%d".formatted("2025-01-12T00:00:00", "2025-01-22T23:59:59", 3);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").value(3));
    }

    @Test
    public void getShiftsByDateRangeAndUserIdAsNotOwnerTest() throws Exception {
        String url = "/shift/range?start=%s&end=%s&user_id=%d".formatted("2025-01-12T00:00:00", "2025-01-22T23:59:59", 10);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getShiftsByDateRangeAndUserIdInvalidDateTest() throws Exception {
        String url = "/shift/range?start=%s&end=%s&user_id=%d".formatted("test", "test", 1);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getShiftsByDateRangeAndUserIdInverseDateTest() throws Exception {
        String url = "/shift/range?start=%s&end=%s&user_id=%d".formatted("2025-01-22T23:59:59", "2025-01-12T00:00:00", 1);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addShiftAsAdminTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ShiftDto shiftDto = ShiftDto.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(1).plusHours(8))
                .userId(3L)
                .roleName("Kelner")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/shift")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(shiftDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.role_name").value("Kelner"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value(3));
    }

    @Test
    public void addShiftAsUserTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ShiftDto shiftDto = ShiftDto.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(1).plusHours(8))
                .userId(3L)
                .roleName("Kelner")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/shift")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(shiftDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void addShiftDateInPastTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ShiftDto shiftDto = ShiftDto.builder()
                .start(now.minusDays(1))
                .end(now.minusDays(1).minusDays(8))
                .userId(3L)
                .roleName("Kelner")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/shift")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(shiftDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addShiftInvalidUserTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ShiftDto shiftDto = ShiftDto.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(1).plusHours(8))
                .userId(0L)
                .roleName("Kelner")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/shift")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(shiftDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateShiftAsAdminTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ShiftDto shiftDto = ShiftDto.builder()
                .id(49L)
                .start(now.plusDays(1))
                .end(now.plusDays(1).plusHours(8))
                .userId(17L)
                .roleName("Kelner")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/shift/{id}", 49)
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(shiftDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(49))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role_name").value("Kelner"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value(17));
    }

    @Test
    public void updateShiftAsUserTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ShiftDto shiftDto = ShiftDto.builder()
                .id(49L)
                .start(now.plusDays(1))
                .end(now.plusDays(1).plusHours(8))
                .userId(3L)
                .roleName("Kelner")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/shift/{id}", 49)
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(shiftDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void updateShiftInvalidUserTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ShiftDto shiftDto = ShiftDto.builder()
                .id(49L)
                .start(now.plusDays(1))
                .end(now.plusDays(1).plusHours(8))
                .userId(0L)
                .roleName("Kelner")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/shift/{id}", 49)
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(shiftDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void publishShiftsAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/shift/{start}/{end}", "2025-01-12", "2025-01-22")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].published").value(true));
    }

    @Test
    public void publishShiftsAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/shift/{start}/{end}", "2025-01-12", "2025-01-22")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void publishShiftsInvalidDateTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/shift/{start}/{end}", "test", "test")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deleteShiftAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/shift/{id}", 49)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteShiftAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/shift/{id}", 49)
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void deleteShiftInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/shift/{id}", 0)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
