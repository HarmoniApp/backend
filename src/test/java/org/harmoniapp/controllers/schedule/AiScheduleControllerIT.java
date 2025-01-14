package org.harmoniapp.controllers.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.schedule.aischedule.ReqRoleDto;
import org.harmoniapp.contracts.schedule.aischedule.ReqShiftDto;
import org.harmoniapp.contracts.schedule.aischedule.ScheduleRequirement;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Integration tests for the {@link AiScheduleController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class AiScheduleControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;
    private static String jwtUser;
    private static ObjectMapper mapper;

    @Autowired
    public AiScheduleControllerIT(MockMvc mockMvc) {
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
    public void generateScheduleSuccessfulTest() throws Exception {
        // Given
        List<ReqRoleDto> roles = Stream.of(
                        new ReqRoleDto(2L, 1),
                        new ReqRoleDto(3L, 1))
                .collect(Collectors.toList());
        List<ReqShiftDto> shift = Stream.of(
                        new ReqShiftDto(1L, roles),
                        new ReqShiftDto(2L, roles))
                .collect(Collectors.toList());
        LocalDate date = LocalDate.now();
        List<ScheduleRequirement> requirements = Stream.of(
                        new ScheduleRequirement(date.plusDays(1), shift),
                        new ScheduleRequirement(date.plusDays(2), shift))
                .collect(Collectors.toList());

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/aiSchedule/generate")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requirements)))
                //Then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
    }

    @Test
    public void generateScheduleRepeatedRequirementTest() throws Exception {
        // Given
        List<ReqRoleDto> roles = Stream.of(new ReqRoleDto(2L, 1)).collect(Collectors.toList());
        List<ReqShiftDto> shift = Stream.of(new ReqShiftDto(2L, roles)).collect(Collectors.toList());
        LocalDate date = LocalDate.now();
        List<ScheduleRequirement> requirements = Stream.of(
                        new ScheduleRequirement(date.plusDays(1), shift),
                        new ScheduleRequirement(date.plusDays(1), shift))
                .collect(Collectors.toList());

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/aiSchedule/generate")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requirements)))
                //Then
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void generateScheduleInvalidQuantityTest() throws Exception {
        // Given
        List<ReqRoleDto> roles = Stream.of(new ReqRoleDto(2L, 99)).collect(Collectors.toList());
        List<ReqShiftDto> shift = Stream.of(new ReqShiftDto(2L, roles)).collect(Collectors.toList());
        LocalDate date = LocalDate.now();
        List<ScheduleRequirement> requirements = Stream.of(
                        new ScheduleRequirement(date.plusDays(1), shift),
                        new ScheduleRequirement(date.plusDays(2), shift))
                .collect(Collectors.toList());

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/aiSchedule/generate")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requirements)))
                //Then
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void generateScheduleInvalidRoleIdTest() throws Exception {
        // Given
        List<ReqRoleDto> roles = Stream.of(new ReqRoleDto(0L, 1)).collect(Collectors.toList());
        List<ReqShiftDto> shift = Stream.of(new ReqShiftDto(1L, roles)).collect(Collectors.toList());
        LocalDate date = LocalDate.now();
        List<ScheduleRequirement> requirements = Stream.of(
                        new ScheduleRequirement(date.plusDays(1), shift),
                        new ScheduleRequirement(date.plusDays(2), shift))
                .collect(Collectors.toList());

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/aiSchedule/generate")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requirements)))
                //Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void generateScheduleInvalidShiftIdTest() throws Exception {
        // Given
        List<ReqRoleDto> roles = Stream.of(new ReqRoleDto(2L, 1)).collect(Collectors.toList());
        List<ReqShiftDto> shift = Stream.of(new ReqShiftDto(0L, roles)).collect(Collectors.toList());
        LocalDate date = LocalDate.now();
        List<ScheduleRequirement> requirements = Stream.of(
                        new ScheduleRequirement(date.plusDays(1), shift),
                        new ScheduleRequirement(date.plusDays(2), shift))
                .collect(Collectors.toList());

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/aiSchedule/generate")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requirements)))
                //Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void generateScheduleAsUserTest() throws Exception {
        List<ReqRoleDto> roles = Stream.of(new ReqRoleDto(2L, 1)).collect(Collectors.toList());
        List<ReqShiftDto> shift = Stream.of(new ReqShiftDto(2L, roles)).collect(Collectors.toList());
        LocalDate date = LocalDate.now();
        List<ScheduleRequirement> requirements = Stream.of(new ScheduleRequirement(date.plusDays(1), shift))
                .collect(Collectors.toList());


        mockMvc.perform(MockMvcRequestBuilders.post("/aiSchedule/generate")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requirements)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
