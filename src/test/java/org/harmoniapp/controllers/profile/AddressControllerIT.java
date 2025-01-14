package org.harmoniapp.controllers.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.profile.AddressDto;
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
 * Integration tests for the {@link AddressController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class AddressControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;

    @Autowired
    public AddressControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public static void setUp(@Autowired LoginService loginService) {
        // Login as an admin to get a JWT token
        var credentialsAdmin = new LoginRequestDto("jan.kowalski@example.com", "StrongPassword!2137");
        jwtAdmin = loginService.login(credentialsAdmin).jwtToken();
    }

    @Test
    public void getAllAddressesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/address")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void getAllDepartmentsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/address/departments/name")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(4));
    }

    @Test
    public void getAllDepartmentsAddressTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/address/departments")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(4));
    }

    @Test
    public void getAddressTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/address/1")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void getAddressIncorrectIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/address/0")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void createAddressCorrectTest() throws Exception {
        AddressDto addressDto = AddressDto.builder()
                .zipCode("00-001")
                .city("Warsaw")
                .street("Marszałkowska")
                .buildingNumber("1")
                .build();
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/address")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(addressDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.zip_code").value("00-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("Warsaw"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Marszałkowska"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.building_number").value("1"));
    }

    @Test
    public void createAddressMissingArgsTest() throws Exception {
        AddressDto addressDto = AddressDto.builder()
                .zipCode("00-001")
                .build();
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/address")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(addressDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateAddressCorrectTest() throws Exception {
        AddressDto addressDto = AddressDto.builder()
                .zipCode("00-001")
                .city("Warsaw")
                .street("Marszałkowska")
                .buildingNumber("1")
                .build();
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/address/1")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(addressDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zip_code").value("00-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("Warsaw"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Marszałkowska"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.building_number").value("1"));
    }

    @Test
    public void updateNotExistedAddressCorrectTest() throws Exception {
        AddressDto addressDto = AddressDto.builder()
                .zipCode("00-001")
                .city("Warsaw")
                .street("Marszałkowska")
                .buildingNumber("1")
                .build();
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/address/1")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(addressDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.zip_code").value("00-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("Warsaw"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Marszałkowska"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.building_number").value("1"));
    }

    @Test
    public void updateAddressMissingArgsTest() throws Exception {
        AddressDto addressDto = AddressDto.builder()
                .zipCode("00-001")
                .build();
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/address/1")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(addressDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deleteAddressTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/address/1")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteNotExistedAddressTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/address/0")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
