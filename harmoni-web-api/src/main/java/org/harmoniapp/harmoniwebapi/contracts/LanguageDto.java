package org.harmoniapp.harmoniwebapi.contracts;

import org.harmoniapp.harmonidata.entities.Language;

/**
 * Data Transfer Object (DTO) for Language.
 * This record represents a language with an ID and a name.
 * It provides methods to convert between {@link Language} entities and {@link LanguageDto}.
 */
public record LanguageDto(Long id, String name) {

    /**
     * Converts a {@link Language} entity to a {@link LanguageDto}.
     *
     * @param entity the {@link Language} entity to convert.
     * @return a {@link LanguageDto} representing the entity.
     */
    public static LanguageDto fromEntity(final Language entity) {
        return new LanguageDto(entity.getId(), entity.getName());
    }

    /**
     * Converts this {@link LanguageDto} to a {@link Language} entity.
     *
     * @return a {@link Language} entity representing this DTO.
     */
    public Language toEntity() {
        return new Language(id, name);
    }
}
