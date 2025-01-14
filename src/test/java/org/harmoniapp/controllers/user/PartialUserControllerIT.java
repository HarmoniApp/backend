package org.harmoniapp.controllers.user;

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
 * Test class for {@link PartialUserController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class PartialUserControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;
    private static String jwtUser;

    @Autowired
    public PartialUserControllerIT(MockMvc mockMvc) {
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
    public void getUserPageAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/simple")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
    }

    @Test
    public void getUserPageAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/simple")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
    }

    @Test
    public void getUserPageWithParamsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/simple")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .param("pageNumber", "1")
                        .param("pageSize", "10")
                        .param("sortBy", "id")
                        .param("order", "asc"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNumber").value(1));
    }

    @Test
    public void getUserWithOutlierParamsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/simple")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .param("pageNumber", "0")
                        .param("pageSize", "0")
                        .param("sortBy", "id")
                        .param("order", "asc"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
    }

    @Test
    public void getUserAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/simple/1")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void getUserAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/simple/1")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void getUserInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/simple/0")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getUserSearchAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/simple/search")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .param("q", "jan"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void getUserSearchAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/simple/search")
                        .header("Authorization", "Bearer " + jwtUser)
                        .param("q", "jan"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }
}
