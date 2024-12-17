package org.harmoniapp.services.chat;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.chat.ChatRequestDto;
import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.contracts.chat.TranslationRequestDto;
import org.harmoniapp.entities.chat.Group;
import org.harmoniapp.entities.chat.Message;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFound;
import org.harmoniapp.exception.TranslationException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final RepositoryCollector repositoryCollector;
    private final SimpMessagingTemplate messagingTemplate;
    private final TranslationService translationService;

    public List<MessageDto> getChatHistory(ChatRequestDto chatRequestDto, TranslationRequestDto translationRequestDto) {
        assert chatRequestDto.userId1() != null;
        List<Message> messages = getMessages(chatRequestDto);
        return messages.stream()
                .map(message -> mapMessage(message, translationRequestDto))
                .toList();
    }

    public List<Long> getChatPartnersByUserId(Long userId) {
        return repositoryCollector.getMessages().findChatPartners(userId);
    }

    public String getLastMessageByUsersId(ChatRequestDto chatRequestDto) {
        Long groupId = chatRequestDto.groupId();
        Long userId1 = chatRequestDto.userId1();
        Long userId2 = chatRequestDto.userId2();

        if (groupId != null) {
            return repositoryCollector.getMessages().findLastMessageByGroupId(groupId);
        } else if (userId1 != null && userId2 != null) {
            return repositoryCollector.getMessages().findLastMessageByUsersId(userId1, userId2);
        } else {
            //TODO
            throw new IllegalArgumentException("userId2 or groupId must be provided");
        }
    }

    @Transactional
    public MessageDto createMessage(MessageDto messageDto) {
        Message message = messageDto.toEntity();
        setSender(message, messageDto);

        Long receiverId = messageDto.receiverId();
        Long groupId = messageDto.groupId();
        if (receiverId != null) {
            return createDirectMessage(message, receiverId);
        } else if (groupId != null) {
            return createGroupMessage(message, groupId);
        } else {
            //TODO
            throw new IllegalArgumentException("receiverId or groupId must be provided.");
        }
    }

    @Transactional
    public List<MessageDto> markAllMessagesAsRead(ChatRequestDto chatRequestDto) {
        assert chatRequestDto.userId1() != null;
        List<Message> messages = getUnreadMessages(chatRequestDto);
        messages = markAsReadAndSave(messages);

        List<MessageDto> messagesDto = messages.stream()
                .map(MessageDto::fromEntity)
                .toList();

        sendStatusUpdate(messagesDto);
        return messagesDto;
    }

    private List<Message> getMessages(ChatRequestDto chatRequestDto) {
        if (chatRequestDto.groupId() != null) {
            return repositoryCollector.getMessages().findGroupChatHistory(chatRequestDto.groupId());
        } else {
            return repositoryCollector.getMessages().findChatHistory(chatRequestDto.userId1(), chatRequestDto.userId2());
        }
    }

    private MessageDto mapMessage(Message message, TranslationRequestDto translationRequestDto) {
        if (shouldTranslate(translationRequestDto)) {
            try {
                String content = translationService.translate(message.getContent(), translationRequestDto.targetLanguage());
                return MessageDto.fromEntity(message, content);
            } catch (TranslationException ignored) {
            }
        }
        return MessageDto.fromEntity(message);
    }

    private boolean shouldTranslate(TranslationRequestDto translationRequestDto) {
        return translationRequestDto.translate()
                && translationRequestDto.targetLanguage() != null
                && !translationRequestDto.targetLanguage().isEmpty();
    }

    private void setSender(Message message, MessageDto inputMessage) {
        User sender = getUserById(inputMessage.senderId());
        message.setSender(sender);
    }

    private User getUserById(long id) {
        return repositoryCollector.getUsers().findByIdAndIsActive(id, true)
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono użytkownika o ID: " + id));
    }

    private MessageDto createDirectMessage(Message message, long receiverId) {
        User receiver = getUserById(receiverId);
        message.setReceiver(receiver);
        MessageDto messageDto = saveMessage(message);
        sendMessage("/client/messages/" + messageDto.receiverId(), messageDto);
        return messageDto;
    }

    private MessageDto createGroupMessage(Message message, long groupId) {
        Group group = getGroupById(groupId);
        message.setGroup(group);
        MessageDto messageDto = saveMessage(message);
        sendMessageToGroup(group, messageDto);
        return messageDto;
    }

    //TODO: extract to separate service and make it async
    private void sendMessageToGroup(Group group, MessageDto messageDto) {
        group.getMembers()
                .stream()
                .filter(member -> !member.getId().equals(messageDto.senderId()))
                .forEach(member -> sendMessage("/client/groupMessages/" + member.getId(), messageDto));
    }

    private MessageDto saveMessage(Message message) {
        Message savedMessage = repositoryCollector.getMessages().save(message);
        return MessageDto.fromEntity(savedMessage);
    }

    private void sendMessage(String destination, MessageDto message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    private void sendMessage(String destination, List<MessageDto> message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    private Group getGroupById(long id) {
        return repositoryCollector.getGroups().findById(id)
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono grupy o ID: " + id));
    }

    private List<Message> getUnreadMessages(ChatRequestDto chatRequestDto) {
        long userId1 = chatRequestDto.userId1();
        Long userId2 = chatRequestDto.userId2();
        Long groupId = chatRequestDto.groupId();
        if (chatRequestDto.groupId() != null) {
            return repositoryCollector.getMessages().findUnreadByGroupId(userId1, groupId);
        } else {
            return repositoryCollector.getMessages().findUnreadByUsersIds(userId1, userId2);
        }
    }

    private List<Message> markAsReadAndSave(List<Message> messages) {
        messages.forEach(message -> message.setRead(true));
        return repositoryCollector.getMessages().saveAll(messages);
    }

    //TODO: extract to separate service and make it async
    private void sendStatusUpdate(List<MessageDto> messages) {
        if (messages.getFirst().groupId() != null) {
            Group group = getGroupById(messages.getFirst().groupId());
            group.getMembers().stream()
                    .filter(member -> !member.getId().equals(messages.getFirst().senderId()))
                    .forEach(member -> sendMessage("/client/groupMessages/readStatus/" + member.getId(), messages));
        } else {
            long userId2 = messages.getFirst().receiverId();
            sendMessage("/client/messages/readStatus/" + userId2, messages);
        }
    }
}