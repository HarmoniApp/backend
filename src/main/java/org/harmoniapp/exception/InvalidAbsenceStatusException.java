package org.harmoniapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an absence status is invalid.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAbsenceStatusException extends RuntimeException {
    public InvalidAbsenceStatusException(String message) {
        super(message);
    }
}
