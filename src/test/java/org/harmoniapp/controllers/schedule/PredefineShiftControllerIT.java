package org.harmoniapp.controllers.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.schedule.PredefineShiftDto;
import org.harmoniapp.services.auth.LoginService;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

/**
 * Integration tests for the {@link PredefineShiftController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class PredefineShiftControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;
    private static ObjectMapper mapper;

    @Autowired
    public PredefineShiftControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public static void setUp(@Autowired LoginService loginService) {
        // Login as an admin to get a JWT token
        var credentialsAdmin = new LoginRequestDto("jan.kowalski@example.com", "StrongPassword!2137");
        jwtAdmin = loginService.login(credentialsAdmin).jwtToken();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void getPredefineShiftTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/predefine-shift/1")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void getPredefineShiftInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/predefine-shift/0")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getAllPredefineShiftsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/predefine-shift")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void createPredefineShiftTest() throws Exception {
        PredefineShiftDto predefineShiftDto = PredefineShiftDto.builder()
                .name("Test")
                .start(LocalTime.of(8, 0))
                .end(LocalTime.of(16, 0))
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/predefine-shift")
                .header("Authorization", "Bearer " + jwtAdmin)
                .contentType("application/json")
                .content(mapper.writeValueAsString(predefineShiftDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start").value("08:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end").value("16:00:00"));
    }

    @Test
    public void createPredefineInvalidDataTest() throws Exception {
        PredefineShiftDto predefineShiftDto = PredefineShiftDto.builder()
                .name("Test")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/predefine-shift")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(predefineShiftDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updatePredefineShiftTest() throws Exception {
        PredefineShiftDto predefineShiftDto = PredefineShiftDto.builder()
                .name("Test")
                .start(LocalTime.of(8, 0))
                .end(LocalTime.of(16, 0))
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/predefine-shift/1")
                .header("Authorization", "Bearer " + jwtAdmin)
                .contentType("application/json")
                .content(mapper.writeValueAsString(predefineShiftDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start").value("08:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end").value("16:00:00"));
    }

    @Test
    public void updatePredefineShiftInvalidDataTest() throws Exception {
        PredefineShiftDto predefineShiftDto = PredefineShiftDto.builder()
                .name("Test")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/predefine-shift/1")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(predefineShiftDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updatePredefineShiftInvalidIdTest() throws Exception {
        PredefineShiftDto predefineShiftDto = PredefineShiftDto.builder()
                .name("Test")
                .start(LocalTime.of(8, 0))
                .end(LocalTime.of(16, 0))
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/predefine-shift/99")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(predefineShiftDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start").value("08:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end").value("16:00:00"));
    }

    @Test
    public void deletePredefineShiftTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/predefine-shift/1")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deletePredefineShiftInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/predefine-shift/99")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
