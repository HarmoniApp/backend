package org.harmoniapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing different types of shift notifications.
 */
@Getter
@AllArgsConstructor
public enum ShiftNotificationType {
    PUBLISHED_SHIFT("Zmiana została opublikowana",
            "Nowa zmiana została opublikowana. Zmiana %s - %s została opublikowana. Sprawdź swój harmonogram."),
    DELETED_SHIFT("Zmiana została usunięta",
            "Zmiana %s - %s została usunięta. Sprawdź swój harmonogram.");


    private final String title;
    private final String messageTemplate;

    /**
     * Formats the message template with the provided arguments.
     *
     * @param args the arguments to format the message template
     * @return the formatted message
     */
    public String formatMessage(Object... args) {
        return messageTemplate.formatted(args);
    }
}
