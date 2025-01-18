package org.harmoniapp.services.chat;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.chat.ChatPartnerDto;
import org.harmoniapp.contracts.chat.ChatRequestDto;
import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.contracts.chat.TranslationRequestDto;
import org.harmoniapp.entities.chat.Group;
import org.harmoniapp.entities.chat.Message;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.exception.InvalidConversationException;
import org.harmoniapp.exception.TranslationFailsException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for handling chat messages.
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final RepositoryCollector repositoryCollector;
    private final WebsocketMessageServiceImpl websocketMessageService;
    private final TranslationService translationService;

    /**
     * Retrieves the chat history for a given chat request.
     *
     * @param chatRequestDto        the chat request containing user and group IDs
     * @param translationRequestDto the translation request containing translation preferences
     * @return a list of MessageDto objects representing the chat history
     */
    @Override
    public List<MessageDto> getChatHistory(ChatRequestDto chatRequestDto, TranslationRequestDto translationRequestDto) {
        assert chatRequestDto.userId1() != null;
        List<Message> messages = getMessages(chatRequestDto);
        return messages.stream()
                .map(message -> mapMessage(message, translationRequestDto))
                .toList();
    }

    /**
     * Retrieves all chat partners for a given user.
     *
     * @param userId the ID of the user
     * @return a list of ChatPartnerDto objects representing the chat partners
     * @throws EntityNotFoundException if the user is not found
     */
    @Override
    public List<ChatPartnerDto> getAllChatPartners(long userId) {
        if (!repositoryCollector.getUsers().existsById(userId)) {
            throw new EntityNotFoundException("Nie znaleziono użytkownika o ID: " + userId);
        }
        List<Object[]> results = repositoryCollector.getMessages().findAllChatPartners(userId);
        return results.stream()
                .map(row -> new ChatPartnerDto(((Number) row[0]).longValue(), (String) row[1]))
                .toList();
    }

    /**
     * Retrieves the last message between users or in a group.
     *
     * @param chatRequestDto the chat request containing user and group IDs
     * @return the content of the last message
     * @throws InvalidConversationException if the conversation is invalid
     */
    @Override
    public String getLastMessage(ChatRequestDto chatRequestDto) {
        Long groupId = chatRequestDto.groupId();
        Long userId1 = chatRequestDto.userId1();
        Long userId2 = chatRequestDto.userId2();

        if (groupId != null) {
            return repositoryCollector.getMessages().findLastMessageByGroupId(groupId);
        } else if (userId1 != null && userId2 != null) {
            return repositoryCollector.getMessages().findLastMessageByUsersId(userId1, userId2);
        } else {
            throw new InvalidConversationException("Nieprawidłowy konwersacja");
        }
    }

    /**
     * Creates a new message.
     *
     * @param messageDto the message data transfer object containing message details
     * @return the created MessageDto object
     * @throws InvalidConversationException if the conversation is invalid
     */
    @Override
    @Transactional
    public MessageDto createMessage(MessageDto messageDto) {
        Message message = messageDto.toEntity();
        setSender(message, messageDto);

        Long receiverId = messageDto.receiverId();
        Long groupId = messageDto.groupId();
        if (receiverId != null && receiverId != 0) {
            return createDirectMessage(message, receiverId);
        } else if (groupId != null) {
            return createGroupMessage(message, groupId);
        } else {
            throw new InvalidConversationException("Nieprawidłowy konwersacja");
        }
    }

    /**
     * Marks all unread messages as read for a given chat request.
     *
     * @param chatRequestDto the chat request containing user and group IDs
     * @return a list of MessageDto objects representing the messages that were marked as read
     */
    @Override
    @Transactional
    public List<MessageDto> markAllMessagesAsRead(ChatRequestDto chatRequestDto) {
        assert chatRequestDto.userId1() != null;
        List<Message> messages = getUnreadMessages(chatRequestDto);
        if (messages.isEmpty()) {
            return List.of();
        }
        messages = markAsReadAndSave(messages);
        List<MessageDto> messagesDto = messages.stream()
                .map(MessageDto::fromEntity)
                .toList();

        sendUpdateStatus(messagesDto);
        return messagesDto;
    }

    /**
     * Sends a status update for the given messages.
     *
     * @param messages the list of MessageDto objects representing the messages
     */
    private void sendUpdateStatus(List<MessageDto> messages) {
        if (messages.getFirst().groupId() != null) {
            Group group = getGroupById(messages.getFirst().groupId());
            websocketMessageService.sendStatusUpdate(group, messages);
        } else if (messages.getFirst().receiverId() != null) {
            websocketMessageService.sendStatusUpdate(messages.getFirst().receiverId(), messages);
            websocketMessageService.sendStatusUpdate(messages.getFirst().senderId(), messages);
        }
    }

    /**
     * Retrieves the chat messages based on the provided chat request.
     *
     * @param chatRequestDto the chat request containing user and group IDs
     * @return a list of Message objects representing the chat history
     * @throws InvalidConversationException if the conversation is invalid
     */
    List<Message> getMessages(ChatRequestDto chatRequestDto) {
        Long groupId = chatRequestDto.groupId();
        Long userId1 = chatRequestDto.userId1();
        Long userId2 = chatRequestDto.userId2();
        if (groupId != null) {
            return repositoryCollector.getMessages().findGroupChatHistory(groupId);
        } else if (userId1 != null && userId2 != null) {
            return repositoryCollector.getMessages().findChatHistory(userId1, userId2);
        } else {
            throw new InvalidConversationException("Nieprawidłowy konwersacja");
        }
    }

    /**
     * Maps a Message entity to a MessageDto, optionally translating the message content.
     *
     * @param message               the Message entity to be mapped
     * @param translationRequestDto the translation request containing translation preferences
     * @return a MessageDto object representing the mapped message
     */
    MessageDto mapMessage(Message message, TranslationRequestDto translationRequestDto) {
        if (shouldTranslate(translationRequestDto)) {
            try {
                String content = translationService.translate(message.getContent(), translationRequestDto.targetLanguage());
                return MessageDto.fromEntity(message, content);
            } catch (TranslationFailsException ignored) {
            }
        }
        return MessageDto.fromEntity(message);
    }

    /**
     * Determines if the message should be translated based on the translation request.
     *
     * @param translationRequestDto the translation request containing translation preferences
     * @return true if the message should be translated, false otherwise
     */
    private boolean shouldTranslate(TranslationRequestDto translationRequestDto) {
        return translationRequestDto.translate() != null
                && translationRequestDto.translate()
                && translationRequestDto.targetLanguage() != null
                && !translationRequestDto.targetLanguage().isEmpty();
    }

    /**
     * Sets the sender of the message.
     *
     * @param message      the message entity to set the sender for
     * @param inputMessage the message data transfer object containing sender details
     */
    private void setSender(Message message, MessageDto inputMessage) {
        User sender = getUserById(inputMessage.senderId());
        message.setSender(sender);
    }

    /**
     * Retrieves a user by their ID and ensures the user is active.
     *
     * @param id the ID of the user to retrieve
     * @return the User entity if found and active
     * @throws EntityNotFoundException if no active user is found with the given ID
     */
    private User getUserById(long id) {
        return repositoryCollector.getUsers().findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono użytkownika o ID: " + id));
    }

    /**
     * Creates a direct message between users.
     *
     * @param message    the message entity to be sent
     * @param receiverId the ID of the receiver
     * @return the created MessageDto object
     * @throws EntityNotFoundException if the receiver is not found
     */
    private MessageDto createDirectMessage(Message message, long receiverId) {
        User receiver = getUserById(receiverId);
        message.setReceiver(receiver);
        MessageDto messageDto = saveMessage(message);
        websocketMessageService.sendMessage(messageDto.receiverId(), messageDto);
        return messageDto;
    }

    /**
     * Creates a group message.
     *
     * @param message the message entity to be sent
     * @param groupId the ID of the group
     * @return the created MessageDto object
     * @throws EntityNotFoundException if the group is not found
     */
    private MessageDto createGroupMessage(Message message, long groupId) {
        Group group = getGroupById(groupId);
        message.setGroup(group);
        MessageDto messageDto = saveMessage(message);
        websocketMessageService.sendMessageToGroup(group, messageDto);
        return messageDto;
    }

    /**
     * Saves the given message entity to the repository.
     *
     * @param message the message entity to be saved
     * @return the saved MessageDto object
     */
    private MessageDto saveMessage(Message message) {
        Message savedMessage = repositoryCollector.getMessages().save(message);
        return MessageDto.fromEntity(savedMessage);
    }

    /**
     * Retrieves a group by its ID.
     *
     * @param id the ID of the group to retrieve
     * @return the Group entity if found
     * @throws EntityNotFoundException if no group is found with the given ID
     */
    private Group getGroupById(long id) {
        return repositoryCollector.getGroups().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono grupy o ID: " + id));
    }

    /**
     * Retrieves all unread messages for a given chat request.
     *
     * @param chatRequestDto the chat request containing user and group IDs
     * @return a list of Message objects representing the unread messages
     * @throws InvalidConversationException if the conversation is invalid
     */
    private List<Message> getUnreadMessages(ChatRequestDto chatRequestDto) {
        Long userId1 = chatRequestDto.userId1();
        Long userId2 = chatRequestDto.userId2();
        Long groupId = chatRequestDto.groupId();
        if (userId1 == null) {
            throw new InvalidConversationException("Nieprawidłowy konwersacja");
        }
        if (groupId != null) {
            return repositoryCollector.getMessages().findUnreadByGroupId(userId1, groupId);
        }
        if (userId2 != null) {
            return repositoryCollector.getMessages().findUnreadByUsersIds(userId1, userId2);
        }
        throw new InvalidConversationException("Nieprawidłowy konwersacja");
    }

    /**
     * Marks the given list of messages as read and saves them to the repository.
     *
     * @param messages the list of Message objects to be marked as read
     * @return the list of Message objects after being marked as read and saved
     */
    private List<Message> markAsReadAndSave(List<Message> messages) {
        messages.forEach(message -> message.setRead(true));
        return repositoryCollector.getMessages().saveAll(messages);
    }
}