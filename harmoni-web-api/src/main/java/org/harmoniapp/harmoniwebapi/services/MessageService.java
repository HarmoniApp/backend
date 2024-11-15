package org.harmoniapp.harmoniwebapi.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Group;
import org.harmoniapp.harmonidata.entities.Message;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.MessageDto;
import org.harmoniapp.harmoniwebapi.contracts.PageDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class MessageService {
    private final RepositoryCollector repositoryCollector;
    private final SimpMessagingTemplate messagingTemplate;
    private final TranslationService translationService;

    public PageDto<MessageDto> getChatHistory(Long userId1, Long userId2, Long groupId,
                                              boolean translate, String targetLanguage,
                                              int pageNumber, int pageSize) {
        pageNumber = (pageNumber < 1) ? 0 : pageNumber - 1;
        pageSize = (pageSize < 1) ? 20 : pageSize;
        Sort.Direction sortDirection = Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, "sentAt"));
        Page<Message> messages;

        if (groupId != null) {
            messages = repositoryCollector.getMessages().findGroupChatHistory(groupId, pageable);
        } else {
            messages = repositoryCollector.getMessages().findChatHistory(userId1, userId2, pageable);
        }

        List<MessageDto> messageDtoList = messages.stream()
                .map(message -> {
                    String content = message.getContent();

                    if (translate && targetLanguage != null && !targetLanguage.isEmpty()) {
                        content = translationService.translate(message.getContent(), targetLanguage);
                    }
                    return MessageDto.fromEntity(message, content);
                })
                .sorted(Comparator.comparing(MessageDto::sentAt))
                .toList();
        return new PageDto<>(messageDtoList,
                messages.getSize(),
                messages.getNumber() + 1,
                messages.getTotalPages());
    }


    public List<Long> getChatPartners(Long userId) {
        return repositoryCollector.getMessages().findChatPartners(userId);
    }

    public String getLasMessageByUsersId(Long userId1, Long userId2, Long groupId) {
        if (groupId != null) {
            return repositoryCollector.getMessages().findLastMessageByGroupId(groupId);
        } else if (userId2 != null) {
            return repositoryCollector.getMessages().findLastMessageByUsersId(userId1, userId2);
        } else {
            throw new IllegalArgumentException("userId2 or groupId must be provided");
        }
    }

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

            messagingTemplate.convertAndSend("/client/messages/" + messageDto.receiverId(), MessageDto.fromEntity(savedMessage, null));

            return MessageDto.fromEntity(savedMessage, null);
        } else if (messageDto.groupId() != null) {
            Group group = repositoryCollector.getGroups().findById(messageDto.groupId())
                    .orElseThrow(() -> new IllegalArgumentException("Group not found"));
            message.setGroup(group);

            Message savedMessage = repositoryCollector.getMessages().save(message);

            for (User member : group.getMembers()) {
                if (!member.getId().equals(sender.getId())) {
                    messagingTemplate.convertAndSend(
                            "/client/groupMessages/" + member.getId(),
                            MessageDto.fromEntity(savedMessage, null)
                    );
                }
            }

            return MessageDto.fromEntity(savedMessage, null);
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
    public List<MessageDto> markAllMessagesAsRead(Long userId1, Long userId2, Long groupId) {
        List<Message> messages;

        if (groupId != null) {
            messages = repositoryCollector.getMessages().findUnreadByGroupId(userId1, groupId);
        } else {
            messages = repositoryCollector.getMessages().findUnreadByUsersIds(userId1, userId2);
        }

        messages.forEach(message -> message.setRead(true));
        messages = repositoryCollector.getMessages().saveAll(messages);

        List<MessageDto> messagesDto = messages.stream()
                .map(m -> MessageDto.fromEntity(m, null))
                .toList();

        if (groupId != null) {
            Group group = repositoryCollector.getGroups().findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Group not found"));

            group.getMembers().forEach(member -> {
                if (!member.getId().equals(userId1)) {
                    messagingTemplate.convertAndSend("/client/groupMessages/readStatus/" + member.getId(), messagesDto);
                }
            });
        } else {
            messagingTemplate.convertAndSend("/client/messages/readStatus/" + userId2, messagesDto);
        }

        return messagesDto;
    }

}
