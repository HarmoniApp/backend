package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.LanguageDto;
import org.harmoniapp.harmoniwebapi.services.LanguageService;
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
@CrossOrigin(origins = "http://localhost:3000")
public class LanguageController {
    private final LanguageService service;

    /**
     * Retrieves all languages.
     *
     * @return a list of {@link LanguageDto} representing all languages.
     */
    @GetMapping
    public List<LanguageDto> getAllAddresses() {
        return service.getAllLanguages();
    }

    /**
     * Retrieves a language by its ID.
     *
     * @param id the ID of the language to retrieve.
     * @return the {@link LanguageDto} representing the language.
     */
    @GetMapping("/{id}")
    public LanguageDto getAddress(@PathVariable long id) {
        return service.getLanguageById(id);
    }

    /**
     * Creates a new language.
     *
     * @param dto the {@link LanguageDto} containing the details of the language to create.
     * @return the created {@link LanguageDto}.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LanguageDto createAddress(@RequestBody LanguageDto dto) {
        return service.createLanguage(dto);
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
    public LanguageDto updateAddress(@PathVariable long id, @RequestBody LanguageDto dto) {
        return service.updateLanguage(id, dto);
    }

    /**
     * Deletes a language by its ID.
     *
     * @param id the ID of the language to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable long id) {
        service.deleteLanguage(id);
    }

}
