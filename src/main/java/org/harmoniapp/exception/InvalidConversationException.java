package org.harmoniapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CREATED)
public class InvalidConversationException extends RuntimeException {
    public InvalidConversationException(String message) {
        super(message);
    }
}
