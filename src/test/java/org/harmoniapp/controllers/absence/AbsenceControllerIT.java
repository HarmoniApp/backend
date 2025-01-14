package org.harmoniapp.controllers.absence;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Test class for {@link AbsenceController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class AbsenceControllerIT {

    private final MockMvc mockMvc;
    private static Long userId = 3L;
    private static String jwtAdmin;
    private static String jwtUser;

    @Autowired
    public AbsenceControllerIT(MockMvc mockMvc) {
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
    public void getAbsenceByUserIdReturnsCorrectElementTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/absence/user/3")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].user_id").value(3));
    }

    @Test
    public void getAbsenceByUserIdWithCorrectPageSizeAndNumberReturnsCorrectElementTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/absence/user/2")
                        .param("pageSize", "1")
                        .param("pageNumber", "2")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNumber").value(2));
    }

    @Test
    public void getAbsenceByUserIdWithNegativePageSizeAndNumberReturnsCorrectElementTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/absence/user/2")
                        .param("pageSize", "-1")
                        .param("pageNumber", "-2")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNumber").value(1));
    }

    @Test
    public void getAbsenceByStatusCorrectElementTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/absence/status/1")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].status.id").value(1));
    }

    @Test
    public void getAllAbsencesReturnsCorrectElementsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/absence")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
    }

    @Test
    public void createAbsenceCorrectlyTest() throws Exception {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String start = now.plusDays(1).format(formatter);
        String end = now.plusDays(10).format(formatter);
        String absenceJson = "{\"user_id\": %s, \"absence_type_id\": 1, \"start\": \"%s\", \"end\": \"%s\"}"
                .formatted(userId, start, end);
        mockMvc.perform(MockMvcRequestBuilders.post("/absence")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType("application/json")
                        .content(absenceJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.absence_type_id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start").value(start))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end").value(end))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.submission").value(now.format(formatter)));
    }

    @Test
    public void createAbsenceDateInPastTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/absence")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType("application/json")
                        .content("{\"user_id\": 1, \"absence_type_id\": 1, \"start\": \"2022-01-01\", \"end\": \"2021-01-01\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createAbsenceToManyDaysTest() throws Exception {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String start = now.plusDays(1).format(formatter);
        String end = now.plusDays(120).format(formatter);
        String absenceJson = "{\"user_id\": %s, \"absence_type_id\": 1, \"start\": \"%s\", \"end\": \"%s\"}"
                .formatted(userId, start, end);

        mockMvc.perform(MockMvcRequestBuilders.post("/absence")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType("application/json")
                        .content(absenceJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateAbsenceStatusApprovedSuccessfulTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/absence/1/status/2")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.id").value(2));
    }

    @Test
    public void updateAbsenceStatusRejectedSuccessfulTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/absence/1/status/4")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.id").value(4));
    }

    @Test
    public void updateAbsenceStatusFailedTryModifyRequestInPastTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/absence/2/status/2")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateAbsenceStatusBadStatusIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/absence/3/status/1")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deleteAbsenceSuccessfulTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/absence/1/status/3")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteAbsenceBadStatusIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/absence/1/status/1")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deleteAbsenceForbiddenAccessAsNotOwnerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/absence/8/status/3")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void deleteAbsenceFailedTryUpdateApprovedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/absence/3/status/3")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
