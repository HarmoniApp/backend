package org.harmoniapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to modify an admin role.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AdminRoleModificationException extends RuntimeException {
    public AdminRoleModificationException(String message) {
        super(message);
    }
}
