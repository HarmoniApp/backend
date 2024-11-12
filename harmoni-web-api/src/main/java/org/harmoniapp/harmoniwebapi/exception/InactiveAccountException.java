package org.harmoniapp.harmoniwebapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InactiveAccountException extends RuntimeException {
    public InactiveAccountException(String message) {
        super(message);
    }

    public InactiveAccountException() {
        super("Account not found");
    }
}
