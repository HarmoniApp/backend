package org.harmoniapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing different types of absence notifications.
 */
@Getter
@AllArgsConstructor
public enum AbsenceNotificationType {
    NEW_ABSENCE("Nowy wniosek o urlop",
            "Nowy wniosek o urlop. Pracownik %s %s złożył wniosek o urlop. Zapoznaj się ze zmianami"),
    EMPLOYER_UPDATED("Urlop zaaktualizowany",
            "Status urlopu zaaktualizowany. Status urlopu %s - %s to %s. Zapoznaj się ze zmianami."),
    EMPLOYEE_DELETED("Urlop anulowany",
            "Pracownik %s %s anulował urlop");

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
