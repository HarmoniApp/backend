package org.harmoniapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing different statuses of absences.
 */
@Getter
@AllArgsConstructor
public enum AbsenceStatus {
    AWAITING("Oczekuje", 1L),
    APPROVED("Zatwierdzony", 2L),
    CANCELLED("Anulowany", 3L),
    REJECTED("Odrzucony", 4L);

    private final String name;
    private final Long id;
}
