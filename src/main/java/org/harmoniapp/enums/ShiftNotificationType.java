package org.harmoniapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShiftNotificationType {
    PUBLISHED_SHIFT("Zmiana została opublikowana",
            "Nowa zmiana została opublikowana. Zmiana %s - %s została opublikowana. Sprawdź swój harmonogram."),
    DELETED_SHIFT("Zmiana została usunięta",
            "Zmiana %s - %s została usunięta. Sprawdź swój harmonogram.");


    private final String title;
    private final String messageTemplate;

    public String formatMessage(Object... args) {
        return messageTemplate.formatted(args);
    }
}
