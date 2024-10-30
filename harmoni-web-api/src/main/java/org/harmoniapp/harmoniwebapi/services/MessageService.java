package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Message;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.MessageDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class MessageService {
    private final RepositoryCollector repositoryCollector;
    private final SimpMessagingTemplate messagingTemplate;


    public Message sendMessage(MessageDto messageDto) {
        Message message = messageDto.toEntity();

        message.setSenderId(messageDto.senderId());
        message.setReceiverId(messageDto.receiverId());
        message.setContent(messageDto.content());
        message.setSentAt(LocalDateTime.now());

        Message savedMessage = repositoryCollector.getMessages().save(message);

        messagingTemplate.convertAndSendToUser(messageDto.receiverId().toString(), "/client/messages", savedMessage);

        return savedMessage;
    }

    public List<Message> getChatHistory(Long userId1, Long userId2) {
        return repositoryCollector.getMessages().findChatHistory(userId1, userId2);
    }

    public List<Long> getChatPartners(Long userId) {
        return repositoryCollector.getMessages().findChatPartners(userId);
    }
}
