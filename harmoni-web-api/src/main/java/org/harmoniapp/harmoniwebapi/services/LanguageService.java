package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Language;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.LanguageDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing languages.
 * This service provides methods for CRUD operations on languages.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class LanguageService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves all languages from the repository, sorted by their name in ascending order.
     *
     * @return a list of {@link LanguageDto} representing all languages.
     */
    public List<LanguageDto> getAllLanguages() {
        List<Language> languages = repositoryCollector.getLanguages().findAll(Sort.by("name"));

        return languages.stream()
                .map(LanguageDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves a language by its ID.
     *
     * @param id the ID of the language to retrieve.
     * @return the {@link LanguageDto} representing the language.
     * @throws IllegalArgumentException if the language with the specified ID does not exist.
     */
    public LanguageDto getLanguageById(long id) {
        Language language =  repositoryCollector.getLanguages()
                .findById(id)
                .orElseThrow(IllegalArgumentException::new);

        return LanguageDto.fromEntity(language);
    }

    /**
     * Creates a new language.
     *
     * @param languageDto the {@link LanguageDto} containing the details of the language to create.
     * @return the created {@link LanguageDto}.
     * @throws IllegalArgumentException if a language with the same name already exists.
     */
    public LanguageDto createLanguage(LanguageDto languageDto) {
        Language language = languageDto.toEntity();

        if (repositoryCollector.getLanguages().findByNameIgnoreCase(language.getName()).orElse(null) != null) {
            throw new IllegalArgumentException("Language already exists");
        }

        language = repositoryCollector.getLanguages().save(language);

        return LanguageDto.fromEntity(language);
    }

    /**
     * Updates an existing language by its ID.
     *
     * @param id the ID of the language to update.
     * @param languageDto the {@link LanguageDto} containing the updated details of the language.
     * @return the updated {@link LanguageDto}.
     */
    public LanguageDto updateLanguage(long id, LanguageDto languageDto) {
        Language language = repositoryCollector.getLanguages().findById(id).orElse(null);

        if (language == null) {
            language = languageDto.toEntity();
        } else {
            language.setName(languageDto.name());
        }

        language = repositoryCollector.getLanguages().save(language);

        return LanguageDto.fromEntity(language);
    }

    /**
     * Deletes a language by its ID.
     * If the language is associated with any users, it removes the language from those users before deleting it.
     *
     * @param id the ID of the language to delete.
     * @throws IllegalArgumentException if the language with the specified ID does not exist.
     */
    public void deleteLanguage(long id) {
        Language language = repositoryCollector.getLanguages().findById(id).orElseThrow(IllegalArgumentException::new);

        List<User> users = repositoryCollector.getUsers().findByLanguages_Id(id);
        if (!users.isEmpty()) {
            users.forEach(user -> user.getLanguages().remove(language));
            repositoryCollector.getUsers().saveAll(users);
        }

        repositoryCollector.getLanguages().deleteById(id);
    }
}
