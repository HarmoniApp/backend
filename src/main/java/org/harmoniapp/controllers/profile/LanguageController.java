package org.harmoniapp.controllers.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.services.profile.LanguageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing language-related endpoints.
 * This controller provides RESTful endpoints for CRUD operations on languages.
 * It uses {@link LanguageService} to handle the business logic.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("language")
public class LanguageController {
    private final LanguageService service;

    /**
     * Retrieves all languages.
     *
     * @return a list of {@link LanguageDto} representing all languages.
     */
    @GetMapping
    public List<LanguageDto> getAllLanguages() {
        return service.getAll();
    }

    /**
     * Retrieves a language by its ID.
     *
     * @param id the ID of the language to retrieve.
     * @return the {@link LanguageDto} representing the language.
     */
    @GetMapping("/{id}")
    public LanguageDto getLanguage(@PathVariable long id) {
        return service.getById(id);
    }

    /**
     * Creates a new language.
     *
     * @param dto the {@link LanguageDto} containing the details of the language to create.
     * @return the created {@link LanguageDto}.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LanguageDto createLanguage(@Valid @RequestBody LanguageDto dto) {
        return service.create(dto);
    }

    /**
     * Updates an existing language by its ID.
     *
     * @param id the ID of the language to update.
     * @param dto the {@link LanguageDto} containing the updated details of the language.
     * @return the updated {@link LanguageDto}.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public LanguageDto updateLanguage(@PathVariable long id, @Valid @RequestBody LanguageDto dto) {
        return service.updateById(id, dto);
    }

    /**
     * Deletes a language by its ID.
     *
     * @param id the ID of the language to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLanguage(@PathVariable long id) {
        service.deleteById(id);
    }

}
