package org.harmoniapp.controllers.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.profile.LanguageDto;
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

/**
 * Integration tests for the {@link LanguageController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class LanguageControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;
    private static String jwtUser;

    @Autowired
    public LanguageControllerIT(MockMvc mockMvc) {
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
    public void getAllLanguagesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/language")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void getLanguageTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/language/1")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void getLanguageInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/language/0")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void createLanguageAsAdminTest() throws Exception {
        LanguageDto languageDto = new LanguageDto(null, "TestLanguage", "TL");
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/language")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(languageDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestLanguage"));
    }

    @Test
    public void createLanguageAsUserTest() throws Exception {
        LanguageDto languageDto = new LanguageDto(null, "TestLanguage", "TL");
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/language")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(languageDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void createLanguageAlreadyExistTest() throws Exception {
        LanguageDto languageDto = new LanguageDto(null, "Angielski", "gb");
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/language")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(languageDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateLanguageTest() throws Exception {
        LanguageDto languageDto = new LanguageDto(1L, "TestLanguage", "TL");
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/language/1")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(languageDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestLanguage"));
    }

    @Test
    public void updateLanguageInvalidIdTest() throws Exception {
        LanguageDto languageDto = new LanguageDto(0L, "TestLanguage", "TL");
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/language/0")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(languageDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestLanguage"));
    }

    @Test
    public void deleteLanguageTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/language/1")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteLanguageInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/language/0")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
