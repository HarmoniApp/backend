package org.harmoniapp.harmoniwebapi.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Group;
import org.harmoniapp.harmonidata.entities.Message;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.MessageDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class MessageService {
    private final RepositoryCollector repositoryCollector;
    private final SimpMessagingTemplate messagingTemplate;
    private final TranslationService translationService;

//    public List<MessageDto> getChatHistory(Long userId1, Long userId2) {
//        List<Message> messages= repositoryCollector.getMessages().findChatHistory(userId1, userId2);
//        return messages.stream()
//                .map(MessageDto::fromEntity)
//                .toList();
//    }

//    public List<MessageDto> getChatHistory(Long userId1, Long userId2, boolean translate, String targetLanguage) {
//        List<Message> messages = repositoryCollector.getMessages().findChatHistory(userId1, userId2);
//
//        return messages.stream()
//                .map(message -> {
//                    String content = message.getContent();
//
//                    if (translate && targetLanguage != null && !targetLanguage.isEmpty()) {
//                        content = translationService.translate(message.getContent(), targetLanguage);
//                    }
//
//                    return new MessageDto(
//                            message.getId(),
//                            message.getSender().getId(),
//                            message.getReceiver() != null ? message.getReceiver().getId() : null,
//                            message.getGroup() != null ? message.getGroup().getId() : null,
//                            content,
//                            message.getSentAt(),
//                            message.isRead()
//                    );
//                }).toList();
//    }

    public List<MessageDto> getChatHistory(Long userId1, Long userId2, Long groupId, boolean translate, String targetLanguage) {
        List<Message> messages;

        if (groupId != null) {
            messages = repositoryCollector.getMessages().findGroupChatHistory(groupId);
        } else {
            messages = repositoryCollector.getMessages().findChatHistory(userId1, userId2);
        }

        return messages.stream()
                .map(message -> {
                    String content = message.getContent();

                    if (translate && targetLanguage != null && !targetLanguage.isEmpty()) {
                        content = translationService.translate(message.getContent(), targetLanguage);
                    }

                    return new MessageDto(
                            message.getId(),
                            message.getSender().getId(),
                            message.getReceiver() != null ? message.getReceiver().getId() : null,
                            message.getGroup() != null ? message.getGroup().getId() : null,
                            content,
                            message.getSentAt(),
                            message.isRead()
                    );
                }).toList();
    }


    public List<Long> getChatPartners(Long userId) {
        return repositoryCollector.getMessages().findChatPartners(userId);
    }

    public String getLasMessageByUsersId(Long userId1, Long userId2) {
        return  repositoryCollector.getMessages().findLastMessageByUsersId(userId1, userId2);
    }

//    @Transactional
//    public MessageDto sendMessage(MessageDto messageDto) {
//        User sender = repositoryCollector.getUsers().findById(messageDto.senderId())
//                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
//
//        User receiver = repositoryCollector.getUsers().findById(messageDto.receiverId())
//                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
//
//        Message message = messageDto.toEntity();
//
//        message.setSender(sender);
//        message.setReceiver(receiver);
//
//        Message savedMessage = repositoryCollector.getMessages().save(message);
//
//        messagingTemplate.convertAndSend("/client/messages/" + messageDto.receiverId(), MessageDto.fromEntity(savedMessage));
//
//        return MessageDto.fromEntity(savedMessage);
//    }

    @Transactional
    public MessageDto sendMessage(MessageDto messageDto) {
        User sender = repositoryCollector.getUsers().findById(messageDto.senderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        Message message = messageDto.toEntity();
        message.setSender(sender);

        if (messageDto.receiverId() != null) {
            User receiver = repositoryCollector.getUsers().findById(messageDto.receiverId())
                    .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
            message.setReceiver(receiver);

            Message savedMessage = repositoryCollector.getMessages().save(message);

            messagingTemplate.convertAndSend("/client/messages/" + messageDto.receiverId(), MessageDto.fromEntity(savedMessage));

            return MessageDto.fromEntity(savedMessage);
        } else if (messageDto.groupId() != null) {
            Group group = repositoryCollector.getGroups().findById(messageDto.groupId())
                    .orElseThrow(() -> new IllegalArgumentException("Group not found"));
            message.setGroup(group);

            Message savedMessage = repositoryCollector.getMessages().save(message);

            messagingTemplate.convertAndSend("/client/messages/group/" + group.getId(), MessageDto.fromEntity(savedMessage));

            return MessageDto.fromEntity(savedMessage);
        } else {
            throw new IllegalArgumentException("receiverId or groupId must be provided.");
        }
    }



//    @Transactional
//    public MessageDto markMessageAsRead(Long messageId) {
//        Message message = repositoryCollector.getMessages().findById(messageId)
//                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
//
//        message.setRead(true);
//        Message updatedMessage = repositoryCollector.getMessages().save(message);
//
//        messagingTemplate.convertAndSend("/client/messages/read-status/" + message.getSender().getId(), MessageDto.fromEntity(updatedMessage));
//
//        return MessageDto.fromEntity(updatedMessage);
//    }

    @Transactional
    public List<MessageDto> markAllMessagesAsRead(Long userId1, Long userId2) {
        List<Message> messages = repositoryCollector.getMessages().findUnreadByUsersIds(userId1, userId2);
        messages.forEach(message -> message.setRead(true));
        repositoryCollector.getMessages().saveAll(messages);

        List<MessageDto> messagesDto = messages.stream()
                .map(MessageDto::fromEntity)
                .toList();

        messagingTemplate.convertAndSend("/client/messages/read-status/" + userId2, messagesDto);

        return messagesDto;
    }

}
