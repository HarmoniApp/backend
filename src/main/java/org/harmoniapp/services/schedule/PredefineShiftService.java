package org.harmoniapp.services.schedule;

import org.harmoniapp.contracts.schedule.PredefineShiftDto;

import java.util.List;

/**
 * Service interface for managing predefined shifts.
 */
public interface PredefineShiftService {

    /**
     * Retrieves a predefined shift by its ID.
     *
     * @param id the ID of the predefined shift
     * @return the predefined shift DTO
     */
    PredefineShiftDto get(long id);

    /**
     * Retrieves all predefined shifts.
     *
     * @return a list of predefined shift DTOs
     */
    List<PredefineShiftDto> getAll();

    /**
     * Creates a new predefined shift.
     *
     * @param predefineShiftDto the predefined shift DTO to create
     * @return the created predefined shift DTO
     */
    PredefineShiftDto create(PredefineShiftDto predefineShiftDto);

    /**
     * Updates an existing predefined shift.
     *
     * @param id the ID of the predefined shift to update
     * @param predefineShiftDto the predefined shift DTO with updated information
     * @return the updated predefined shift DTO
     */
    PredefineShiftDto update(long id, PredefineShiftDto predefineShiftDto);

    /**
     * Deletes a predefined shift by its ID.
     *
     * @param id the ID of the predefined shift to delete
     */
    void delete(long id);
}
