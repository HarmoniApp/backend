package org.harmoniapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a language already exists.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LanguageExistsException extends RuntimeException {
    public LanguageExistsException(String message) {
        super(message);
    }
}
