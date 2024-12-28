package org.harmoniapp.contracts.chat;

import org.springframework.web.bind.annotation.RequestParam;

/**
 * Data Transfer Object for chat requests.
 *
 * @param userId1 the ID of the first user (optional)
 * @param userId2 the ID of the second user (optional)
 * @param groupId the ID of the group (optional)
 */
public record ChatRequestDto(@RequestParam(required = false) Long userId1,
                             @RequestParam(required = false) Long userId2,
                             @RequestParam(required = false) Long groupId) {
}
