package org.harmoniapp.controllers.chat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.chat.MessageDto;
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
    public List<MessageDto> getChatHistory(@RequestParam Long userId1,
                                              @RequestParam(required = false) Long userId2,
                                              @RequestParam(required = false) Long groupId,
                                              @RequestParam(defaultValue = "false") boolean translate,
                                              @RequestParam(required = false) String targetLanguage) {
        return service.getChatHistory(userId1, userId2, groupId, translate, targetLanguage);
    }

    @GetMapping("/chat-partners")
    public List<Long> getChatPartners(@RequestParam Long userId) {
        return service.getChatPartners(userId);
    }

    @GetMapping("/last")
    public String getLasMessageByUsersId(@RequestParam(required = false) Long userId1,
                                         @RequestParam(required = false) Long userId2,
                                         @RequestParam(required = false) Long groupId) {
        return service.getLasMessageByUsersId(userId1, userId2, groupId);
    }

    @PostMapping("/send")
    @PreAuthorize("@securityService.canSendMessage(#messageDto, authentication)")
    public MessageDto sendMessage(@Valid @RequestBody MessageDto messageDto) {
        return service.sendMessage(messageDto);
    }

    @PatchMapping("/mark-all-read")
    @PreAuthorize("@securityService.canMarkAllMessagesAsRead(#userId1, #groupId, authentication)")
    public List<MessageDto> markAllRead(@RequestParam Long userId1,
                                        @RequestParam(required = false) Long userId2,
                                        @RequestParam(required = false) Long groupId) {
        return service.markAllMessagesAsRead(userId1, userId2, groupId);
    }
}
