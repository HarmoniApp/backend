package org.harmoniapp.harmoniwebapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user provides an insecure or compromised password.
 * <p>
 * This exception is used to indicate that the password is either too weak or has been compromised in known data breaches.
 * It results in a <code>400 Bad Request</code> HTTP response when thrown.
 * </p>
 *
 * <p>HTTP Status: {@link HttpStatus#BAD_REQUEST}</p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EasyPasswordException extends RuntimeException {

    /**
     * Constructs a new {@code EasyPasswordException} with a custom error message.
     *
     * @param message the custom error message describing why the password is considered insecure.
     */
    public EasyPasswordException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code EasyPasswordException} with a default error message.
     */
    public EasyPasswordException() {
        super("Password is too easy or has been pwned!");
    }
}
