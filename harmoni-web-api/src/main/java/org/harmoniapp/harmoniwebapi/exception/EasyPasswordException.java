package org.harmoniapp.harmoniwebapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EasyPasswordException extends RuntimeException {
    public EasyPasswordException(String message) {
        super(message);
    }

    public EasyPasswordException() {
        super("Password is too easy or has been pwned!");
    }
}
