package org.harmoniapp.harmoniwebapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there are not enough employees to generate a schedule.
 * <p>
 * This exception is used to indicate that there are not enough employees to generate a schedule based on the requirements.
 * It results in a <code>409 Conflict</code> HTTP response when thrown.
 * </p>
 *
 * <p>HTTP Status: {@link HttpStatus#CONFLICT}</p>
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class NotEnoughEmployees extends RuntimeException {
    public NotEnoughEmployees(String message) {
        super(message);
    }
}
