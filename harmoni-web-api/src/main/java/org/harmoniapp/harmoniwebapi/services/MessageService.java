package org.harmoniapp.harmoniwebapi.services;

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


    public MessageDto sendMessage(MessageDto messageDto) {
        User sender = repositoryCollector.getUsers().findById(messageDto.senderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        User receiver = repositoryCollector.getUsers().findById(messageDto.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Message message = messageDto.toEntity();

        message.setSender(sender);
        message.setReceiver(receiver);

        Message savedMessage = repositoryCollector.getMessages().save(message);

        messagingTemplate.convertAndSendToUser(messageDto.receiverId().toString(), "/client/messages", MessageDto.fromEntity(savedMessage));

        return MessageDto.fromEntity(savedMessage);
    }

    public List<MessageDto> getChatHistory(Long userId1, Long userId2) {
        List<Message> messages= repositoryCollector.getMessages().findChatHistory(userId1, userId2);
        return messages.stream()
                .map(MessageDto::fromEntity)
                .toList();
    }

    public List<Long> getChatPartners(Long userId) {
        return repositoryCollector.getMessages().findChatPartners(userId);
    }
}
