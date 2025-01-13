package org.harmoniapp.controllers.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.chat.GroupDto;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests for the {@link GroupController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class GroupControllerIT {

    private final MockMvc mockMvc;
    private static String jwt;

    @Autowired
    public GroupControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public static void setUp(@Autowired LoginService loginService) {
        // Login as a user to get a JWT token
        var credentials = new LoginRequestDto("jan.kowalski@example.com", "StrongPassword!2137");
        jwt = loginService.login(credentials).jwtToken();
    }

    @Test
    public void getGroupByIdAsGroupMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/group/details/{groupId}", 1)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Innovators Collective"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.membersIds").isArray());
    }

    @Test
    public void getGroupByIdIncorrectIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/group/details/{groupId}", 999)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getGroupByIdAsNotGroupMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/group/details/{groupId}", 2)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getGroupByIdInvalidJwtTokenTest() throws Exception {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/group/details/{groupId}", 1)
                            .header("Authorization", "Bearer invalidToken"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        });
    }

    @Test
    public void getGroupMembersByIdAsGroupMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/group/{groupId}/members", 1)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(5));
    }

    @Test
    public void getGroupMembersByIdIncorrectIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/group/{groupId}/members", 999)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getGroupMembersByIdAsNotGroupMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/group/{groupId}/members", 2)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getGroupMembersByIdInvalidJwtTokenTest() throws Exception {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/group/{groupId}/members", 1)
                            .header("Authorization", "Bearer invalidToken"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        });
    }

    @Test
    public void createGroupValidDataTest() throws Exception {
        String groupName = "Test Group";
        GroupDto dto = new GroupDto(0L, groupName, List.of(1L, 2L, 3L));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/group")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(groupName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.membersIds").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.membersIds.length()").value(3));
    }

    @Test
    public void createGroupInvalidDataTest() throws Exception {
        GroupDto dto = new GroupDto(0L, "", List.of());
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/group")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createGroupInvalidUserDataTest() throws Exception {
        GroupDto dto = new GroupDto(0L, "Test group", List.of(99L));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/group")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void createGroupInvalidJwtTokenTest() throws Exception {
        GroupDto dto = new GroupDto(0L, "Test group", List.of(1L, 2L, 3L));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);

        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/group")
                            .header("Authorization", "Bearer invalidToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        });
    }

    @Test
    public void addMemberAsGroupMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/group/{groupId}/user/{userId}/add", 1, 9)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.membersIds").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.membersIds").value(Matchers.hasItem(9)));
    }

    @Test
    public void addMemberAsNotGroupMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/group/{groupId}/user/{userId}/add", 2, 1)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void addMemberInvalidUserIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/group/{groupId}/user/{userId}/add", 1, 99)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addMemberInvalidGroupIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/group/{groupId}/user/{userId}/add", 99, 1)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void addMemberInvalidJwtTokenTest() {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.patch("/group/{groupId}/user/{userId}/add", 1, 6)
                            .header("Authorization", "Bearer invalidToken"))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        });
    }

    @Test
    public void removeMemberAsGroupMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/group/{groupId}/user/{userId}/remove", 1, 5)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.membersIds").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.membersIds").value(Matchers.not(Matchers.hasItem(5))));
    }

    @Test
    public void removeMemberAndDeleteAsGroupMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/group/{groupId}/user/{userId}/remove", 9, 1)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    public void removeMemberAsNotGroupMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/group/{groupId}/user/{userId}/remove", 2, 1)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void removeMemberInvalidUserIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/group/{groupId}/user/{userId}/remove", 1, 99)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void removeMemberInvalidGroupIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/group/{groupId}/user/{userId}/remove", 99, 1)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void removeMemberInvalidJwtTokenTest() {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.patch("/group/{groupId}/user/{userId}/remove", 1, 5)
                            .header("Authorization", "Bearer invalidToken"))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        });
    }

    @Test
    public void deleteGroupAsGroupMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/group/{groupId}", 1)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteGroupAsNotGroupMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/group/{groupId}", 2)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void deleteGroupInvalidGroupIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/group/{groupId}", 99)
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void deleteGroupInvalidJwtTokenTest() {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.delete("/group/{groupId}", 1)
                            .header("Authorization", "Bearer invalidToken"))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        });
    }
}
