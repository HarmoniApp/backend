package org.harmoniapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the AI schedule requirements are invalid
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidAiScheduleRequirementsException extends RuntimeException {
    public InvalidAiScheduleRequirementsException(String message) {
        super(message);
    }
}
