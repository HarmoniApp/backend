package org.harmoniapp.controllers.chat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.chat.ChatPartnerDto;
import org.harmoniapp.contracts.chat.ChatRequestDto;
import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.contracts.chat.TranslationRequestDto;
import org.harmoniapp.services.chat.MessageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    private final MessageService service;

    @GetMapping("/history")
    public List<MessageDto> getChatHistory(@ModelAttribute ChatRequestDto chatRequestDto,
                                           @ModelAttribute TranslationRequestDto translationRequestDto) {
        return service.getChatHistory(chatRequestDto, translationRequestDto);
    }

    @GetMapping("/all-chat-partners")
    public List<ChatPartnerDto> getAllChatPartners(@RequestParam Long userId) {
        return service.getAllChatPartners(userId);
    }

    @GetMapping("/last")
    public String getLasMessageByUsersId(@ModelAttribute ChatRequestDto chatRequestDto) {
        return service.getLastMessageByUsersId(chatRequestDto);
    }

    @PostMapping("/send")
    @PreAuthorize("@securityService.canSendMessage(#messageDto, authentication)")
    public MessageDto sendMessage(@Valid @RequestBody MessageDto messageDto) {
        return service.createMessage(messageDto);
    }

    @PatchMapping("/mark-all-read")
    @PreAuthorize("@securityService.canMarkAllMessagesAsRead(#userId1, #groupId, authentication)") //TODO
    public List<MessageDto> markAllRead(@ModelAttribute ChatRequestDto chatRequestDto) {
        return service.markAllMessagesAsRead(chatRequestDto);
    }
}
