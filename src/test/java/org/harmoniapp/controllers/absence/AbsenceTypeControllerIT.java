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

/**
 * Integration tests for the {@link AbsenceTypeController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class AbsenceTypeControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;
    private static String jwtUser;

    @Autowired
    public AbsenceTypeControllerIT(MockMvc mockMvc) {
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
    public void getAbsenceTypeAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/absence-type/{id}", 1L)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }

    @Test
    public void getAbsenceTypeAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/absence-type/{id}", 1L)
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }

    @Test
    public void getAbsenceTypeMissingElementTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/absence-type/{id}", 100L)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getAllAbsenceTypesAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/absence-type")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(9));
    }

    @Test
    public void getAllAbsenceTypesAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/absence-type")
                        .header("Authorization", "Bearer " + jwtUser)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(9));
    }
}
