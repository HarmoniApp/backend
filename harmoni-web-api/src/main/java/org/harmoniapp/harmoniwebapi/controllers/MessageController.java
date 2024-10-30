package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Message;
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

    @GetMapping("/history")
    public List<Message> getChatHistory(@RequestParam Long userId1, @RequestParam Long userId2) {
        return service.getChatHistory(userId1, userId2);
    }

    @GetMapping("/chat-partners")
    public List<Long> getChatPartners(@RequestParam Long userId) {
        return service.getChatPartners(userId);
    }

    @PostMapping("/send")
    public Message sendMessage(@RequestBody MessageDto messageDto) {
        return service.sendMessage(messageDto);
    }
}
