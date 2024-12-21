package org.harmoniapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user provides an insecure or compromised password.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EasyPasswordException extends RuntimeException {
    public EasyPasswordException() {
        super("Password is too easy or has been pwned!");
    }
}
