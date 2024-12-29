package org.harmoniapp.services.absence;

import org.harmoniapp.contracts.absence.AbsenceTypeDto;

import java.util.List;

/**
 * Service interface for managing absence types.
 */
public interface AbsenceTypeService {

    /**
     * Retrieves an absence type by its ID.
     *
     * @param id the ID of the absence type
     * @return the absence type DTO
     */
    AbsenceTypeDto getAbsenceType(long id);

    /**
     * Retrieves all absence types.
     *
     * @return a list of absence type DTOs
     */
    List<AbsenceTypeDto> getAllAbsenceTypes();
}
