package org.harmoniapp.harmoniwebapi.contracts;

import org.harmoniapp.harmonidata.enums.Language;

import java.util.Set;

public record UserLanguageDto(long id, String firstName, String lastName, Set<Language> languages) {
}
