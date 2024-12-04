package org.harmoniapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCellException extends RuntimeException {
    public InvalidCellException(String message) {
        super(message);
    }
}
