package org.harmoniapp.services.chat;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.entities.chat.Group;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for handling WebSocket messages.
 */
@Service
@RequiredArgsConstructor
public class WebsocketMessageServiceImpl implements WebsocketMessageService {
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Sends a message to a specific receiver.
     *
     * @param receiverId the ID of the receiver to send the message to
     * @param message    the message to be sent
     */
    @Override
    @Async
    public void sendMessage(long receiverId, MessageDto message) {
        sendMessage("/client/messages/" + receiverId, message);
    }

    /**
     * Sends a message to all members of the specified group, except the sender.
     *
     * @param group      the group to send the message to
     * @param messageDto the message to be sent
     */
    @Override
    @Async
    public void sendMessageToGroup(Group group, MessageDto messageDto) {
        group.getMembers()
                .stream()
                .filter(member -> !member.getId().equals(messageDto.senderId()))
                .forEach(member -> sendMessage("/client/groupMessages/" + member.getId(), messageDto));
    }

    /**
     * Sends a status update to all members of the specified group, except the sender.
     *
     * @param group    the group to send the status update to
     * @param messages the list of messages to be sent as status updates
     */
    @Override
    @Async
    public void sendStatusUpdate(Group group, List<MessageDto> messages) {
        group.getMembers().stream()
                .filter(member -> !member.getId().equals(messages.getFirst().senderId()))
                .forEach(member -> sendMessage("/client/groupMessages/readStatus/" + member.getId(), messages));
    }

    /**
     * Sends a status update to a specific receiver.
     *
     * @param receiverId the ID of the receiver to send the status update to
     * @param messages   the list of messages to be sent as status updates
     */
    @Override
    @Async
    public void sendStatusUpdate(long receiverId, List<MessageDto> messages) {
        sendMessage("/client/messages/readStatus/" + receiverId, messages);
    }

    /**
     * Sends a message to the specified destination.
     *
     * @param destination the destination to send the message to
     * @param message     the message to be sent
     */
    private void sendMessage(String destination, MessageDto message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * Sends a list of messages to the specified destination.
     *
     * @param destination the destination to send the messages to
     * @param message     the list of messages to be sent
     */
    private void sendMessage(String destination, List<MessageDto> message) {
        messagingTemplate.convertAndSend(destination, message);
    }
}
