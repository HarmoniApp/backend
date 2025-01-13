package org.harmoniapp.controllers.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.profile.ContractTypeDto;
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

import javax.print.attribute.standard.Media;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests for the {@link ContractTypeController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ContractTypeControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;
    private static String jwtUser;

    @Autowired
    public ContractTypeControllerIT(MockMvc mockMvc) {
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
    public void getContractTypeByIdAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/contract-type/{id}", 1)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void getContractTypeByIdAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/contract-type/{id}", 1)
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getContractTypeByIdMissingIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/contract-type/{id}", 999)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getContractTypeByIdWithoutJwtTokenTest() {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/contract-type/{id}", 1))
                    .andDo(MockMvcResultHandlers.print());
        });
    }

    @Test
    public void getAllContractTypesAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/contract-type")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(5));
    }

    @Test
    public void getAllContractTypesAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/contract-type")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getAllContractTypeWithoutJwtTokenTest() {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/contract-type"))
                    .andDo(MockMvcResultHandlers.print());
        });
    }

    @Test
    public void createContractTypeAsAdminTest() throws Exception {
        ContractTypeDto contractTypeDto = new ContractTypeDto(0, "Test", 5);
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/contract-type")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(contractTypeDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.absence_days").value(5));
    }

    @Test
    public void createContractTypeInvalidDataTest() throws Exception {
        ContractTypeDto contractTypeDto = new ContractTypeDto(0, "Test12", -5);
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/contract-type")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(contractTypeDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createContractTypeAsUserTest() throws Exception {
        ContractTypeDto contractTypeDto = new ContractTypeDto(0, "Test", 5);
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/contract-type")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(contractTypeDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void createContractTypeWithoutJwtTokenTest() {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/contract-type"))
                    .andDo(MockMvcResultHandlers.print());
        });
    }

    @Test
    public void updateContractTypeAsAdminTest() throws Exception{
        ContractTypeDto contractTypeDto = new ContractTypeDto(1, "Test", 5);
        ObjectMapper mapper = new ObjectMapper();


        mockMvc.perform(MockMvcRequestBuilders.put("/contract-type/{id}", 1)
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(contractTypeDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.absence_days").value(5));

    }

    @Test
    public void updateContractTypeInvalidDataTest() throws Exception {
        ContractTypeDto contractTypeDto = new ContractTypeDto(1, "Test12", -5);
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/contract-type/{id}", 1)
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(contractTypeDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateContractTypeMissingIdTest() throws Exception {
        ContractTypeDto contractTypeDto = new ContractTypeDto(999, "Test", 5);
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/contract-type/{id}", 999)
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(contractTypeDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.absence_days").value(5));
    }

    @Test
    public void updateContractTypeAsUserTest() throws Exception {
        ContractTypeDto contractTypeDto = new ContractTypeDto(1, "Test", 5);
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/contract-type/{id}", 1)
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(contractTypeDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void updateContractTypeWithoutJwtTokenTest() {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.put("/contract-type/{id}", 1))
                    .andDo(MockMvcResultHandlers.print());
        });
    }

    @Test
    public void deleteContractTypeAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/contract-type/{id}", 1)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteContractTypeMissingIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/contract-type/{id}", 999)
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteContractTypeAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/contract-type/{id}", 1)
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void deleteContractTypeWithoutJwtTokenTest() {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.delete("/contract-type/{id}", 1))
                    .andDo(MockMvcResultHandlers.print());
        });
    }
}
