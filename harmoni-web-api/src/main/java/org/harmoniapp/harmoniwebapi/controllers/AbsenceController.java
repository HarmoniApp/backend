package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.AbsenceDto;
import org.harmoniapp.harmoniwebapi.services.AbsenceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing absence.
 * Provides endpoints to retrieve absence information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/absence")
@CrossOrigin(origins = "http://localhost:3000")
public class AbsenceController {
    private final AbsenceService absenceService;

    @GetMapping
    public List<AbsenceDto> getAllAbsences() {
        return absenceService.getAllAbsences();
    }

    /**
     * Creates a new Absence.
     *
     * @param absenceDto the AbsenceDto containing the details of the absence to create
     * @return the created AbsenceDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AbsenceDto createAbsence(@RequestBody AbsenceDto absenceDto) {
        return absenceService.createAbsence(absenceDto);
    }

    /**
     * Updates an existing Absence or creates a new one if it doesn't exist.
     *
     * @param id       the ID of the absence to update
     * @param absenceDto the AbsenceDto containing the details of the absence to update
     * @return the updated or newly created AbsenceDto
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public AbsenceDto updateAbsence(@PathVariable long id, @RequestBody AbsenceDto absenceDto) {
        return absenceService.updateAbsence(id, absenceDto);
    }

    /**
     * Updates the status of an existing Absence identified by its ID.
     *
     * @param id the ID of the absence to update
     * @param absenceDto the AbsenceDto containing the new status of the absence
     * @return the updated AbsenceDto
     */
    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.CREATED)
    public AbsenceDto updateAbsenceStatus(@PathVariable long id, @RequestBody AbsenceDto absenceDto) {
        return absenceService.updateAbsenceStatus(id, absenceDto);
    }

}
