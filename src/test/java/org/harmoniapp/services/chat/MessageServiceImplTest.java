// MessageServiceImplTest.java
package org.harmoniapp.services.chat;

import org.harmoniapp.contracts.chat.ChatPartnerDto;
import org.harmoniapp.contracts.chat.ChatRequestDto;
import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.contracts.chat.TranslationRequestDto;
import org.harmoniapp.entities.chat.Group;
import org.harmoniapp.entities.chat.Message;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.chat.GroupRepository;
import org.harmoniapp.repositories.chat.MessageRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private WebsocketMessageServiceImpl websocketMessageService;

    @Mock
    private TranslationService translationService;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    public void getChatHistoryWithoutTranslationTest() {
        ChatRequestDto chatRequestDto = mock(ChatRequestDto.class);
        TranslationRequestDto translationRequestDto = mock(TranslationRequestDto.class);
        Message message = mock(Message.class);
        User user = mock(User.class);
        when(chatRequestDto.userId1()).thenReturn(1L);
        when(chatRequestDto.userId2()).thenReturn(2L);
        when(chatRequestDto.groupId()).thenReturn(null);
        when(message.getSender()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(repositoryCollector.getMessages()).thenReturn(messageRepository);
        when(messageRepository.findChatHistory(1L, 2L)).thenReturn(List.of(message));
        when(messageService.getMessages(chatRequestDto)).thenReturn(List.of(message));
        List<MessageDto> result = messageService.getChatHistory(chatRequestDto, translationRequestDto);

        assertNotNull(result);
    }

    @Test
    public void getChatHistoryWithTranslationTest() {
        ChatRequestDto chatRequestDto = mock(ChatRequestDto.class);
        TranslationRequestDto translationRequestDto = mock(TranslationRequestDto.class);
        Message message = mock(Message.class);
        User user = mock(User.class);
        when(translationRequestDto.translate()).thenReturn(true);
        when(translationRequestDto.targetLanguage()).thenReturn("en");
        when(chatRequestDto.userId1()).thenReturn(1L);
        when(chatRequestDto.userId2()).thenReturn(2L);
        when(chatRequestDto.groupId()).thenReturn(null);
        when(message.getSender()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(repositoryCollector.getMessages()).thenReturn(messageRepository);
        when(messageRepository.findChatHistory(1L, 2L)).thenReturn(List.of(message));
        when(messageService.getMessages(chatRequestDto)).thenReturn(List.of(message));
        when(translationService.translate(any(), any())).thenReturn("translated message");
        List<MessageDto> result = messageService.getChatHistory(chatRequestDto, translationRequestDto);

        assertNotNull(result);
    }

    @Test
    public void getAllChatPartnersTest() {
        long userId = 1L;
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(new Object[]{2L, "Alice"});
        mockResult.add(new Object[]{3L, "Bob"});
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(repositoryCollector.getMessages()).thenReturn(messageRepository);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(messageRepository.findAllChatPartners(userId)).thenReturn(mockResult);

        List<ChatPartnerDto> result = messageService.getAllChatPartners(userId);

        assertNotNull(result);
    }

    @Test
    public void getLastMessageTest() {
        ChatRequestDto chatRequestDto = mock(ChatRequestDto.class);
        when(chatRequestDto.groupId()).thenReturn(1L);
        when(repositoryCollector.getMessages()).thenReturn(messageRepository);
        when(messageRepository.findLastMessageByGroupId(1L)).thenReturn("last message");

        String result = messageService.getLastMessage(chatRequestDto);

        assertNotNull(result);
    }

    @Test
    public void createDirectMessageTest() {
        Long receiverId = 1L;
        Long senderId = 2L;
        MessageDto messageDto = mock(MessageDto.class);
        Message message = mock(Message.class);
        User sender = mock(User.class);
        when(messageDto.toEntity()).thenReturn(message);
        when(messageDto.receiverId()).thenReturn(receiverId);
        when(messageDto.senderId()).thenReturn(senderId);
        when(message.getSender()).thenReturn(sender);
        when(message.getReceiver()).thenReturn(User.builder().id(receiverId).build());
        when(sender.getId()).thenReturn(senderId);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdAndIsActiveTrue(anyLong())).thenReturn(Optional.of(new User()));
        when(repositoryCollector.getMessages()).thenReturn(messageRepository);
        when(messageRepository.save(message)).thenReturn(message);

        MessageDto result = messageService.createMessage(messageDto);

        assertNotNull(result);
    }

    @Test
    public void createGroupMessageTest() {
        Long groupId = 1L;
        Long senderId = 2L;
        MessageDto messageDto = mock(MessageDto.class);
        Message message = mock(Message.class);
        User sender = mock(User.class);
        when(messageDto.toEntity()).thenReturn(message);
        when(messageDto.groupId()).thenReturn(groupId);
        when(messageDto.senderId()).thenReturn(senderId);
        when(messageDto.receiverId()).thenReturn(null);
        when(message.getSender()).thenReturn(sender);
        when(message.getGroup()).thenReturn(new Group(1L, "group", new HashSet<>(List.of(sender))));
        when(sender.getId()).thenReturn(senderId);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByIdAndIsActiveTrue(anyLong())).thenReturn(Optional.of(new User()));
        when(repositoryCollector.getGroups()).thenReturn(groupRepository);
        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(new Group()));
        when(repositoryCollector.getMessages()).thenReturn(messageRepository);
        when(messageRepository.save(message)).thenReturn(message);

        MessageDto result = messageService.createMessage(messageDto);

        assertNotNull(result);
    }

    @Test
    public void markAllMessagesAsReadTest() {
        ChatRequestDto chatRequestDto = mock(ChatRequestDto.class);
        when(chatRequestDto.userId1()).thenReturn(1L);
        when(chatRequestDto.userId2()).thenReturn(2L);
        when(chatRequestDto.groupId()).thenReturn(null);
        Message message = mock(Message.class);
        MessageDto messageDto = mock(MessageDto.class);
        when(repositoryCollector.getMessages()).thenReturn(messageRepository);
        when(messageRepository.findUnreadByUsersIds(1L, 2L)).thenReturn(List.of(message));
        when(messageDto.groupId()).thenReturn(null);
        when(messageDto.senderId()).thenReturn(1L);
        when(messageDto.receiverId()).thenReturn(2L);
        doNothing().when(websocketMessageService).sendStatusUpdate(anyLong(), anyList());
        when(messageRepository.saveAll(anyList())).thenReturn(List.of(message));

        List<MessageDto> result;
        try (MockedStatic<MessageDto> mockedStatic = mockStatic(MessageDto.class)) {
            mockedStatic.when(() -> MessageDto.fromEntity(message)).thenReturn(messageDto);
            result = messageService.markAllMessagesAsRead(chatRequestDto);
        }

        assertNotNull(result);
    }
}