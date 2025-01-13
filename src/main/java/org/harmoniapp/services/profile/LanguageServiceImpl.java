package org.harmoniapp.services.profile;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.entities.profile.Language;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.exception.LanguageExistsException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing languages.
 */
@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    private final RepositoryCollector repositoryCollector;
    private static final String CACHE_NAME = "languages";

    /**
     * Retrieves all languages sorted by name.
     *
     * @return a list of LanguageDto objects representing all languages.
     */
    @Override
    public List<LanguageDto> getAll() {
        return repositoryCollector.getLanguages()
                .findAll(Sort.by("name"))
                .stream()
                .map(LanguageDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves a language by its ID.
     *
     * @param id the ID of the language to retrieve
     * @return a LanguageDto object representing the language
     * @throws EntityNotFoundException if no language is found with the given ID
     */
    @Override
    public LanguageDto getById(long id) {
        Language language = getLanguageById(id);
        return LanguageDto.fromEntity(language);
    }

    /**
     * Retrieves a language by its ID.
     *
     * @param id the ID of the language to retrieve
     * @return the Language entity
     * @throws EntityNotFoundException if no language is found with the given ID
     */
    private Language getLanguageById(long id) {
        return repositoryCollector.getLanguages()
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono języka o podanym ID: %d".formatted(id)));
    }

    /**
     * Creates a new language.
     *
     * @param languageDto the data transfer object containing the language details
     * @return a LanguageDto object representing the created language
     * @throws LanguageExistsException if a language with the same name already exists
     */
    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public LanguageDto create(LanguageDto languageDto) {
        languageExists(languageDto.name());
        Language language = languageDto.toEntity();
        language = repositoryCollector.getLanguages().save(language);
        return LanguageDto.fromEntity(language);
    }

    /**
     * Checks if a language with the given name already exists.
     *
     * @param name the name of the language to check
     * @throws LanguageExistsException if a language with the same name already exists
     */
    private void languageExists(String name) {
        if(repositoryCollector.getLanguages().existsByNameIgnoreCase(name)) {
            throw new LanguageExistsException("Język o nazwie %s już istnieje".formatted(name));
        }
    }

    /**
     * Updates an existing language.
     *
     * @param id the ID of the language to update
     * @param languageDto the data transfer object containing the updated language details
     * @return a LanguageDto object representing the updated language
     * @throws LanguageExistsException if a language with the same name already exists
     */
    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public LanguageDto updateById(long id, LanguageDto languageDto) {
        try {
            Language language = getLanguageById(id);
            languageExists(languageDto.name());
            language = updateLanguage(language, languageDto);
            return LanguageDto.fromEntity(language);
        } catch (EntityNotFoundException e) {
            return create(languageDto);
        }
    }

    /**
     * Updates the given language entity with the details from the provided LanguageDto.
     *
     * @param language the existing Language entity to update
     * @param languageDto the data transfer object containing the updated language details
     * @return the updated Language entity
     */
    private Language updateLanguage(Language language, LanguageDto languageDto) {
        Language newLanguage = languageDto.toEntity();
        newLanguage.setId(language.getId());
        return repositoryCollector.getLanguages().save(newLanguage);
    }

    /**
     * Deletes a language by its ID.
     *
     * @param id the ID of the language to delete
     */
    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteById(long id) {
        Language language = getLanguageById(id);
        removeLanguageFromUsers(language);
        repositoryCollector.getLanguages().deleteById(language.getId());
    }

    /**
     * Removes the specified language from all users who have it.
     *
     * @param language the Language entity to be removed from users
     */
    private void removeLanguageFromUsers(Language language) {
        List<User> users = repositoryCollector.getUsers().findByLanguages_Id(language.getId());
        if (!users.isEmpty()) {
            users.forEach(user -> user.getLanguages().remove(language));
            repositoryCollector.getUsers().saveAll(users);
        }
    }
}
