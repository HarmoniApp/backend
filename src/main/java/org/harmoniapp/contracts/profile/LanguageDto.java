package org.harmoniapp.contracts.profile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.harmoniapp.entities.profile.Language;

/**
 * A Data Transfer Object for Language.
 *
 * @param id   the unique identifier of the language
 * @param name the name of the language
 * @param code the code of the language
 */
public record LanguageDto(
        Long id,

        @NotEmpty(message = "Nazwa języka nie może być pusta")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ]+$", message = "Nazwa języka musi zawierać tylko litery")
        String name,
        @Pattern(regexp = "^[a-zA-Z]{2}$", message = "Kod języka musi składać się z dwóch liter")
        String code) {

    /**
     * Converts a {@link Language} entity to a {@link LanguageDto}.
     *
     * @param entity the {@link Language} entity to convert.
     * @return a {@link LanguageDto} representing the entity.
     */
    public static LanguageDto fromEntity(final Language entity) {
        return new LanguageDto(entity.getId(), entity.getName(), entity.getCode());
    }

    /**
     * Converts this {@link LanguageDto} to a {@link Language} entity.
     *
     * @return a {@link Language} entity representing this DTO.
     */
    public Language toEntity() {
        return new Language(id, name, code);
    }
}
