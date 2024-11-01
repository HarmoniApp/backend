package org.harmoniapp.harmoniwebapi.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    public List<MessageDto> getChatHistory(Long userId1, Long userId2, boolean translate, String targetLanguage) {
        List<Message> messages = repositoryCollector.getMessages().findChatHistory(userId1, userId2);

        return messages.stream()
                .map(message -> {
                    String content = message.getContent();

                    if (translate && targetLanguage != null && !targetLanguage.isEmpty()) {
                        content = translationService.translate(message.getContent(), targetLanguage);
                    }

                    return new MessageDto(
                            message.getId(),
                            message.getSender().getId(),
                            message.getReceiver().getId(),
                            content,
                            message.getSentAt(),
                            message.isRead()
                    );
                }).toList();
    }

    public List<Long> getChatPartners(Long userId) {
        return repositoryCollector.getMessages().findChatPartners(userId);
    }

    @Transactional
    public MessageDto sendMessage(MessageDto messageDto) {
        User sender = repositoryCollector.getUsers().findById(messageDto.senderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        User receiver = repositoryCollector.getUsers().findById(messageDto.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Message message = messageDto.toEntity();

        message.setSender(sender);
        message.setReceiver(receiver);

        Message savedMessage = repositoryCollector.getMessages().save(message);

        messagingTemplate.convertAndSend("/client/messages/" + messageDto.receiverId(), MessageDto.fromEntity(savedMessage));

        return MessageDto.fromEntity(savedMessage);
    }

    @Transactional
    public MessageDto markMessageAsRead(Long messageId) {
        Message message = repositoryCollector.getMessages().findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        message.setRead(true);
        Message updatedMessage = repositoryCollector.getMessages().save(message);
        return MessageDto.fromEntity(updatedMessage);
    }
}
