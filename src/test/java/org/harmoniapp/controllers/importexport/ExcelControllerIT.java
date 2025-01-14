package org.harmoniapp.controllers.importexport;

import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.controllers.user.PartialUserController;
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
 * Test class for {@link PartialUserController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ExcelControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;

    @Autowired
    public ExcelControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public static void setUp(@Autowired LoginService loginService) {
        // Login as an admin to get a JWT token
        var credentialsAdmin = new LoginRequestDto("jan.kowalski@example.com", "StrongPassword!2137");
        jwtAdmin = loginService.login(credentialsAdmin).jwtToken();
    }

    @Test
    public void exportUserToExcelTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/excel/users/export-excel")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.parseMediaType("application/vnd.ms-excel")));
    }

    @Test
    public void exportShiftsToExcelTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/excel/shifts/export-excel")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .param("start", "2025-01-01")
                        .param("end", "2025-01-31"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.parseMediaType("application/vnd.ms-excel")));
    }

    @Test
    public void exportShiftsToExcelDataNotFoundTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/excel/shifts/export-excel")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .param("start", "2024-01-01")
                        .param("end", "2024-01-31"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void exportShiftsToExcelInvalidDateTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/excel/shifts/export-excel")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .param("start", "test")
                        .param("end", "test"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
