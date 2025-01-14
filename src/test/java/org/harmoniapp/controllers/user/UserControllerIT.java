package org.harmoniapp.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.profile.AddressDto;
import org.harmoniapp.contracts.profile.ContractTypeDto;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.contracts.user.UserDto;
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
import java.util.List;

/**
 * Test class for {@link UserController} class
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class UserControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;
    private static String jwtUser;
    private static ObjectMapper mapper;

    @Autowired
    public UserControllerIT(MockMvc mockMvc) {
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
    public void getAllUsersAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].password").doesNotExist());
    }

    @Test
    public void getAllUsersAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getAllUserWithQueryParamTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .param("pageNumber", "0")
                        .param("pageSize", "20"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(20));
    }

    @Test
    public void getUserAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/1")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].password").doesNotExist());
    }

    @Test
    public void getUserAsOwnerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/3")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3));
    }

    @Test
    public void getUserAsNotOwnerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/1")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getUserInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/0")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getUserSearchAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/search")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .param("q", "John"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void createUserAsAdminTest() throws Exception {
        //given
        LocalDate now = LocalDate.now();
        AddressDto residence = AddressDto.builder()
                .city("Warsaw")
                .street("Example Street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .build();
        AddressDto workAddress = AddressDto.builder().id(34L).build();
        UserDto userDto = UserDto.builder()
                .firstname("John")
                .surname("Doe")
                .email("example@example.com")
                .contractType(new ContractTypeDto(1L, null, 0))
                .contractSignature(now.plusDays(1))
                .contractExpiration(now.plusYears(1))
                .residence(residence)
                .workAddress(workAddress)
                .supervisorId(1L)
                .phoneNumber("123456789")
                .employeeId("123456")
                .roles(List.of(new RoleDto(1L, null, null), new RoleDto(2L, null, null)))
                .languages(List.of(new LanguageDto(1L, null, null), new LanguageDto(2L, null, null)))
                .build();

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andDo(MockMvcResultHandlers.print())
                //then
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").isString());
    }

    @Test
    public void createUserAsUserTest() throws Exception {
        //given
        LocalDate now = LocalDate.now();
        AddressDto residence = AddressDto.builder()
                .city("Warsaw")
                .street("Example Street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .build();
        AddressDto workAddress = AddressDto.builder().id(34L).build();
        UserDto userDto = UserDto.builder()
                .firstname("John")
                .surname("Doe")
                .email("example@example.com")
                .contractType(new ContractTypeDto(1L, null, 0))
                .contractSignature(now.plusDays(1))
                .contractExpiration(now.plusYears(1))
                .residence(residence)
                .workAddress(workAddress)
                .supervisorId(1L)
                .phoneNumber("123456789")
                .employeeId("123456")
                .roles(List.of(new RoleDto(1L, null, null), new RoleDto(2L, null, null)))
                .languages(List.of(new LanguageDto(1L, null, null), new LanguageDto(2L, null, null)))
                .build();

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andDo(MockMvcResultHandlers.print())
                //then
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void createUserMissingBodyTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateUserAsAdminTest() throws Exception {
        //given
        LocalDate now = LocalDate.now();
        AddressDto residence = AddressDto.builder()
                .city("Warsaw")
                .street("Example Street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .build();
        AddressDto workAddress = AddressDto.builder().id(34L).build();
        UserDto userDto = UserDto.builder()
                .id(2L)
                .firstname("John")
                .surname("Doe")
                .email("example@example.com")
                .contractType(new ContractTypeDto(1L, null, 0))
                .contractSignature(now.plusDays(1))
                .contractExpiration(now.plusYears(1))
                .residence(residence)
                .workAddress(workAddress)
                .supervisorId(1L)
                .phoneNumber("123456789")
                .employeeId("123456")
                .roles(List.of(new RoleDto(1L, null, null), new RoleDto(2L, null, null)))
                .languages(List.of(new LanguageDto(1L, null, null), new LanguageDto(2L, null, null)))
                .build();

        //when
        mockMvc.perform(MockMvcRequestBuilders.patch("/user/2")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andDo(MockMvcResultHandlers.print())
                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("John"));
    }

    @Test
    public void updateUserInvalidIdTest() throws Exception {
        //given
        LocalDate now = LocalDate.now();
        AddressDto residence = AddressDto.builder()
                .city("Warsaw")
                .street("Example Street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .build();
        AddressDto workAddress = AddressDto.builder().id(34L).build();
        UserDto userDto = UserDto.builder()
                .id(0L)
                .firstname("John")
                .surname("Doe")
                .email("example@example.com")
                .contractType(new ContractTypeDto(1L, null, 0))
                .contractSignature(now.plusDays(1))
                .contractExpiration(now.plusYears(1))
                .residence(residence)
                .workAddress(workAddress)
                .supervisorId(1L)
                .phoneNumber("123456789")
                .employeeId("123456")
                .roles(List.of(new RoleDto(1L, null, null), new RoleDto(2L, null, null)))
                .languages(List.of(new LanguageDto(1L, null, null), new LanguageDto(2L, null, null)))
                .build();

        //when
        mockMvc.perform(MockMvcRequestBuilders.patch("/user/0")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andDo(MockMvcResultHandlers.print())
                //then
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteUserAsAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/2")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteUserAsUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/2")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void deleteUserInvalidIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/0")
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
