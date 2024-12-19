package org.harmoniapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiSchedulerNotificationType {
    SUCCESS("Automatyczne układanie grafiku ukończone",
            "Grafik został pomyślnie wygenerowany, zobacz teraz w kalendarzu."),
    FAILURE("Automatyczne układanie grafiku nie powiodło się",
            "Nie udało się wygenerować grafiku, spróbuj ponownie."),;


    private final String title;
    private final String message;
}
