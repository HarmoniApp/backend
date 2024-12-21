package org.harmoniapp.exception;

/**
 * Exception thrown when message translation fails.
 */
public class TranslationFailsException extends RuntimeException {
    public TranslationFailsException(String message) {
        super(message);
    }
}
