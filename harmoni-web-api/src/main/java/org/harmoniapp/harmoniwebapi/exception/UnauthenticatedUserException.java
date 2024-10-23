package org.harmoniapp.harmoniwebapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user fails to authenticate.
 * <p>
 * This exception is used to indicate that the user could not be authenticated,
 * resulting in a <code>403 Forbidden</code> HTTP response when thrown.
 * </p>
 *
 * <p>HTTP Status: {@link HttpStatus#UNAUTHORIZED}</p>
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthenticatedUserException extends RuntimeException {
    public UnauthenticatedUserException(String message) {
        super(message);
    }
}
