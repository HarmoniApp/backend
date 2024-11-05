package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.MessageDto;
import org.harmoniapp.harmoniwebapi.services.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("message")
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {
    private final MessageService service;

//    @GetMapping("/history")
//    public List<MessageDto> getChatHistory(@RequestParam Long userId1, @RequestParam Long userId2) {
//        return service.getChatHistory(userId1, userId2);
//    }

    @GetMapping("/history")
    public List<MessageDto> getChatHistory(
            @RequestParam Long userId1,
            @RequestParam Long userId2,
            @RequestParam(defaultValue = "false") boolean translate,
            @RequestParam(required = false) String targetLanguage) {
        return service.getChatHistory(userId1, userId2, translate, targetLanguage);
    }


    @GetMapping("/chat-partners")
    public List<Long> getChatPartners(@RequestParam Long userId) {
        return service.getChatPartners(userId);
    }

    @GetMapping("/last")
    public String getLasMessageByUsersId(@RequestParam Long userId1, @RequestParam Long userId2) {
        return service.getLasMessageByUsersId(userId1, userId2);
    }

    @PostMapping("/send")
    public MessageDto sendMessage(@RequestBody MessageDto messageDto) {
        return service.sendMessage(messageDto);
    }

    @PatchMapping("/{id}/read")
    public MessageDto markMessageAsRead(@PathVariable Long id) {
        return service.markMessageAsRead(id);
    }
}
