package org.harmoniapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a conversation is invalid.
 */
@ResponseStatus(HttpStatus.CREATED)
public class InvalidConversationException extends RuntimeException {
    public InvalidConversationException(String message) {
        super(message);
    }
}
