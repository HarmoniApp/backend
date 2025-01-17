package org.harmoniapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a file cannot be generated.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileGenerationException extends RuntimeException {
    public FileGenerationException(String message) {
        super(message);
    }
}
