package org.harmoniapp.services.chat;

import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.entities.chat.Group;
import org.harmoniapp.entities.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebsocketMessageServiceImplTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebsocketMessageServiceImpl websocketMessageService;

    @Test
    public void sendMessageTest() {
        long receiverId = 1L;
        MessageDto message = mock(MessageDto.class);

        websocketMessageService.sendMessage(receiverId, message);

        verify(messagingTemplate, times(1)).convertAndSend("/client/messages/" + receiverId, message);
    }

    @Test
    public void sendMessageToGroupTest() {
        Group group = mock(Group.class);
        MessageDto messageDto = mock(MessageDto.class);
        User member = mock(User.class);
        when(group.getMembers()).thenReturn(new HashSet<>(List.of(member)));
        when(member.getId()).thenReturn(2L);
        when(messageDto.senderId()).thenReturn(1L);

        websocketMessageService.sendMessageToGroup(group, messageDto);

        verify(messagingTemplate, times(1)).convertAndSend("/client/groupMessages/2", messageDto);
    }

    @Test
    public void sendStatusUpdateGroupTest() {
        Group group = mock(Group.class);
        MessageDto messageDto = mock(MessageDto.class);
        List<MessageDto> messages = List.of(messageDto);
        User member = mock(User.class);
        when(group.getMembers()).thenReturn(new HashSet<>(List.of(member)));
        when(member.getId()).thenReturn(2L);
        when(messageDto.senderId()).thenReturn(1L);

        websocketMessageService.sendStatusUpdate(group, messages);

        verify(messagingTemplate, times(1)).convertAndSend("/client/groupMessages/readStatus/2", messages);
    }

    @Test
    public void sendStatusUpdateTest() {
        long receiverId = 1L;
        List<MessageDto> messages = List.of(mock(MessageDto.class));

        websocketMessageService.sendStatusUpdate(receiverId, messages);

        verify(messagingTemplate, times(1)).convertAndSend("/client/messages/readStatus/" + receiverId, messages);
    }
}