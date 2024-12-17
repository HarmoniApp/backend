package org.harmoniapp.contracts.chat;

import org.springframework.web.bind.annotation.RequestParam;

public record ChatRequestDto(@RequestParam(required = false) Long userId1,
                             @RequestParam(required = false) Long userId2,
                             @RequestParam(required = false) Long groupId) {
}
