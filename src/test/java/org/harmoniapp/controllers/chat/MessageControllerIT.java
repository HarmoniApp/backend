package org.harmoniapp.controllers.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.chat.MessageDto;
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
public class MessageControllerIT {

    private final MockMvc mockMvc;
    private static String jwt;

    @Autowired
    public MessageControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public static void setUp(@Autowired LoginService loginService) {
        // Login as a user to get a JWT token
        var credentials = new LoginRequestDto("jan.kowalski@example.com", "StrongPassword!2137");
        jwt = loginService.login(credentials).jwtToken();
    }

    @Test
    public void getIndividualChatHistoryAsMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/message/history?userId1=1&userId2=20")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3));
    }

    @Test
    public void getIndividualChatHistoryTranslatedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/message/history?userId1=1&userId2=20&translate=true&targetLanguage=pl")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3));
    }

    @Test
    public void getIndividualChatHistoryAsNotMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/message/history?userId1=2&userId2=20")
                        .header("Authorization", "Bearer " + jwt)
                        .param("userId1", "2")
                        .param("userId2", "20"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getGroupChatHistoryAsMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/message/history?userId1=1&groupId=1")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].group_id").value(1));
    }

    @Test
    public void getGroupChatHistoryAsNotMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/message/history?userId1=1&groupId=2")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getChatHistoryWithoutJWTTokenTest() throws Exception {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/message/history?userId1=1&userId2=20"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        });
    }

    @Test
    public void getChatHistoryWithoutUserId1Test() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/message/history?userId2=20")
                        .header("Authorization", "Bearer " + jwt)
                        .param("userId2", "20"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getAllChatPartners() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/message/all-chat-partners?userId=1")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(4));
    }

    @Test
    public void getAllChatPartnersWithoutJWTTokenTest() throws Exception {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/message/all-chat-partners?userId=1"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        });
    }

    @Test
    public void getAllChatPartnersWithoutUserIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/message/all-chat-partners")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getAllChatPartnersAsNotMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/message/all-chat-partners?userId=2")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getLastMessageCorrectTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/message/last?userId1=1&userId2=20")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(MockMvcResultMatchers.content().string("Let’s connect later."));
    }

    @Test
    public void getIndividualLastMessageWithoutJWTTokenTest() throws Exception {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/message/last?userId1=1&userId2=20"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        });
    }

    @Test
    public void getGroupLastMessageWithoutJWTTokenTest() throws Exception {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/message/last?userId1=1&groupId=1"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                    .andExpect(MockMvcResultMatchers.content().string("Bye everyone."));
        });
    }

    @Test
    public void getLastMessageWithoutUserId1Test() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/message/last?userId2=20")
                        .header("Authorization", "Bearer " + jwt)
                        .param("userId2", "20"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void sendIndividualMessageTest() throws Exception {
        long senderId = 1L;
        long receiverId = 20L;
        String message = "Hello!";
        MessageDto messageDto = MessageDto.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(message)
                .build();
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/message/send")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(messageDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sender_id").value(senderId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.receiver_id").value(receiverId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(message));
    }

    @Test
    public void sendGroupMessageTest() throws Exception {
        long senderId = 1L;
        long groupId = 1L;
        String message = "Hello!";
        MessageDto messageDto = MessageDto.builder()
                .senderId(senderId)
                .groupId(groupId)
                .content(message)
                .build();
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/message/send")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(messageDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sender_id").value(senderId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.group_id").value(groupId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(message));
    }

    @Test
    public void sendIndividualMessageAsSomeoneElseTest() throws Exception {
        long senderId = 2L;
        long receiverId = 20L;
        String message = "Hello!";
        MessageDto messageDto = MessageDto.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(message)
                .build();
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/message/send")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(messageDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void sendGroupMessageAsNotGroupMemberTest() throws Exception {
        long senderId = 1L;
        long groupId = 2L;
        String message = "Hello!";
        MessageDto messageDto = MessageDto.builder()
                .senderId(senderId)
                .groupId(groupId)
                .content(message)
                .build();
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/message/send")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(messageDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void sendMessageWithoutJWTTokenTest() throws Exception {
        long senderId = 1L;
        long receiverId = 20L;
        String message = "Hello!";
        MessageDto messageDto = MessageDto.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(message)
                .build();
        ObjectMapper mapper = new ObjectMapper();

        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/message/send")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(messageDto)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        });
    }

    @Test
    public void markAllReadIndividualTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/message/mark-all-read?userId1=1&userId2=20")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void markAllReadIndividualAsNotMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/message/mark-all-read?userId1=2&userId2=20")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void markAllReadGroupTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/message/mark-all-read?userId1=1&groupId=1")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void markAllReadGroupAsNotMemberTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/message/mark-all-read?userId1=1&groupId=2")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void markAllReadWithoutJWTTokenTest() throws Exception {
        assertThrows(BadCredentialsException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.patch("/message/mark-all-read?userId1=1&userId2=20"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        });
    }
}
