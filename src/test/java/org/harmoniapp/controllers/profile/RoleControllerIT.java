package org.harmoniapp.controllers.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.services.auth.LoginService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RoleController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class RoleControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;

    @Autowired
    public RoleControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public static void setUp(@Autowired LoginService loginService) {
        // Login as an admin to get a JWT token
        var credentialsAdmin = new LoginRequestDto("jan.kowalski@example.com", "StrongPassword!2137");
        jwtAdmin = loginService.login(credentialsAdmin).jwtToken();
    }

    @Test
    public void getRoleTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/role/1")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void getRoleInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/role/0")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getUserRolesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/role/user/10")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void getUserRolesInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/role/user/0")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getAllRolesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/role")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void createRoleTest() throws Exception {
        RoleDto roleDto = new RoleDto(0L, "TestRole", "#FFFFFF");
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/role")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(roleDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestRole"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value("#FFFFFF"));
    }
    
    @Test
    public void createRoleWithNameAdminTest() throws Exception {
        RoleDto roleDto = new RoleDto(0L, "Admin", "#FFFFFF");
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/role")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(roleDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateRoleTest() throws Exception {
        RoleDto roleDto = new RoleDto(0L, "TestRole", "#FFFFFF");
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/role/2")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(roleDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestRole"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value("#FFFFFF"));
    }

    @Test
    public void updateRoleWithNameAdminTest() throws Exception {
        RoleDto roleDto = new RoleDto(0L, "Admin", "#FFFFFF");
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/role/1")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(roleDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateRoleInvalidIdTest() throws Exception {
        RoleDto roleDto = new RoleDto(0L, "TestRole", "#FFFFFF");
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/role/0")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(roleDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestRole"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value("#FFFFFF"));
    }

    @Test
    public void deleteRoleTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/role/2")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteRoleInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/role/0")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteRoleWithNameAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/role/1")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
