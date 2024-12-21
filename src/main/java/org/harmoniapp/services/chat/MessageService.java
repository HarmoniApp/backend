package org.harmoniapp.services.chat;

import org.harmoniapp.contracts.chat.ChatPartnerDto;
import org.harmoniapp.contracts.chat.ChatRequestDto;
import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.contracts.chat.TranslationRequestDto;

import java.util.List;

/**
 * Service interface for handling chat messages.
 */
public interface MessageService {

    /**
     * Retrieves the chat history for a given chat request and translation request.
     *
     * @param chatRequestDto        the chat request details
     * @param translationRequestDto the translation request details
     * @return a list of message DTOs representing the chat history
     */
    List<MessageDto> getChatHistory(ChatRequestDto chatRequestDto, TranslationRequestDto translationRequestDto);

    /**
     * Retrieves all chat partners for a given user.
     *
     * @param userId the ID of the user
     * @return a list of chat partner DTOs
     */
    List<ChatPartnerDto> getAllChatPartners(long userId);

    /**
     * Retrieves the last message by users' IDs.
     *
     * @param chatRequestDto the chat request details
     * @return the last message as a string
     */
    String getLastMessageByUsersId(ChatRequestDto chatRequestDto);

    /**
     * Creates a new message.
     *
     * @param messageDto the message details
     * @return the created message DTO
     */
    MessageDto createMessage(MessageDto messageDto);

    /**
     * Marks all messages as read for a given chat request.
     *
     * @param chatRequestDto the chat request details
     * @return a list of message DTOs representing the updated messages
     */
    List<MessageDto> markAllMessagesAsRead(ChatRequestDto chatRequestDto);
}
