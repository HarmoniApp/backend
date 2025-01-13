package org.harmoniapp.controllers.chat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.chat.ChatPartnerDto;
import org.harmoniapp.contracts.chat.ChatRequestDto;
import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.contracts.chat.TranslationRequestDto;
import org.harmoniapp.services.chat.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling chat messages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    private final MessageService service;

    /**
     * REST controller for handling chat messages.
     */
    @GetMapping("/history")
    public List<MessageDto> getChatHistory(@ModelAttribute ChatRequestDto chatRequestDto,
                                           @ModelAttribute TranslationRequestDto translationRequestDto) {
        return service.getChatHistory(chatRequestDto, translationRequestDto);
    }

    /**
     * Retrieves all chat partners for a given user.
     *
     * @param userId the ID of the user
     * @return a list of chat partner DTOs
     */
    @GetMapping("/all-chat-partners")
    public List<ChatPartnerDto> getAllChatPartners(@RequestParam @Positive Long userId) {
        return service.getAllChatPartners(userId);
    }

    /**
     * Retrieves the last message between users based on the provided chat request data.
     *
     * @param chatRequestDto the chat request data
     * @return the last message as a string
     */
    @GetMapping("/last")
    public String getLasMessage(@ModelAttribute ChatRequestDto chatRequestDto) {
        return service.getLastMessage(chatRequestDto);
    }

    /**
     * Sends a new message.
     *
     * @param messageDto the message data
     * @return the created message DTO
     */
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@securityService.canSendMessage(#messageDto, authentication)")
    public MessageDto sendMessage(@Valid @RequestBody MessageDto messageDto) {
        return service.createMessage(messageDto);
    }

    /**
     * Marks all messages as read based on the provided chat request data.
     *
     * @param chatRequestDto the chat request data
     * @return a list of message DTOs representing the marked messages
     */
    @PatchMapping("/mark-all-read")
    @PreAuthorize("@securityService.canMarkAllMessagesAsRead(#chatRequestDto, authentication)")
    public List<MessageDto> markAllRead(@ModelAttribute ChatRequestDto chatRequestDto) {
        return service.markAllMessagesAsRead(chatRequestDto);
    }
}
