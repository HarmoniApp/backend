package org.harmoniapp.contracts.profile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.harmoniapp.entities.profile.Language;

/**
 * Data Transfer Object (DTO) for Language.
 * This record represents a language with an ID and a name.
 * It provides methods to convert between {@link Language} entities and {@link LanguageDto}.
 */
public record LanguageDto(
        Long id,

        @NotEmpty(message = "Language name cannot be empty")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Language name must contain only letters")
        String name,
        @Pattern(regexp = "^[a-zA-Z]{2}$", message = "Code must contain only two letters")
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
