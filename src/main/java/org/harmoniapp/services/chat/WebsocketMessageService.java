package org.harmoniapp.services.chat;

import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.entities.chat.Group;

import java.util.List;

/**
 * Service interface for handling WebSocket messages.
 */
public interface WebsocketMessageService {

    /**
     * Sends a message to a specific receiver.
     *
     * @param receiverId the ID of the receiver to send the message to
     * @param message    the message to send
     */
    void sendMessage(long receiverId, MessageDto message);

    /**
     * Sends a message to a specific group.
     *
     * @param group      the group to send the message to
     * @param messageDto the message to send
     */
    void sendMessageToGroup(Group group, MessageDto messageDto);

    /**
     * Sends a status update to a group.
     *
     * @param group    the group to send the status update to
     * @param messages the list of messages to include in the status update
     */
    void sendStatusUpdate(Group group, List<MessageDto> messages);

    /**
     * Sends a status update to a specific receiver.
     *
     * @param receiverId the ID of the receiver to send the status update to
     * @param messages   the list of messages to include in the status update
     */
    void sendStatusUpdate(long receiverId, List<MessageDto> messages);
}
