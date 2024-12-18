package org.harmoniapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AbsenceNotificationType {
    NEW_ABSENCE("Nowy wniosek o urlop",
            "Nowy wniosek o urlop. Pracownik %s %s złożył wniosek o urlop. Zapoznaj się ze zmianami"),
    EMPLOYEE_UPDATED("Urlop zaaktualizowany",
            "Urlop zaaktualizowany. Pracownik %s %s zmienił swoją nieobecność. Zapoznaj się ze zmianami."),
    EMPLOYER_UPDATED("Urlop zaaktualizowany",
            "Status urlopu zaaktualizowany. Status urlopu %s - %s to %s. Zapoznaj się ze zmianami."),
    EMPLOYEE_DELETED("Urlop anulowany",
            "Pracownik %s %s anulował urlop"),
    EMPLOYER_DELETED("Urlop odrzucony",
            "Urlop %s %s - %s odrzucony");

    private final String title;
    private final String messageTemplate;

    public String formatMessage(Object... args) {
        return messageTemplate.formatted(args);
    }
}
