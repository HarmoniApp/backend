package org.harmoniapp.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.AbsenceTypeDto;
import org.harmoniapp.services.AbsenceTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing absenceType.
 * Provides endpoints to retrieve absenceType information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/absence-type")
public class AbsenceTypeController {
    private final AbsenceTypeService absenceTypeService;

    /**
     * Retrieves a absenceType information by ID.
     *
     * @param id the ID of the absenceType to retrieve
     * @return a AbsenceTypeDto containing absenceType information
     */
    @GetMapping("/{id}")
    public AbsenceTypeDto getAbsenceType(@PathVariable long id) {
        return absenceTypeService.getAbsenceType(id);
    }

    /**
     * Retrieves a list of all AbsenceTypeDto.
     *
     * @return a list of AbsenceTypeDto containing the details of all absenceType
     */
    @GetMapping
    public List<AbsenceTypeDto> getAllAbsenceTypes() {
        return absenceTypeService.getAllAbsenceTypes();
    }
}
