package org.harmoniapp.services.schedule;

import org.harmoniapp.contracts.schedule.ShiftDto;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing shifts.
 */
public interface ShiftService {

    /**
     * Retrieves a ShiftDto by its ID.
     *
     * @param id the ID of the shift
     * @return the ShiftDto with the specified ID
     */
    ShiftDto get(long id);

    /**
     * Retrieves all shifts within a specified date range.
     *
     * @param start the start date of the date range
     * @param end   the end date of the date range
     * @return a list of ShiftDto objects within the specified date range
     */
    List<ShiftDto> getShiftsByDateRangeAndUserId(String start, String end, Long userId);

    /**
     * Creates a new ShiftDto.
     *
     * @param shiftDto the ShiftDto to create
     * @return the created ShiftDto
     */
    ShiftDto create(ShiftDto shiftDto);

    /**
     * Updates an existing ShiftDto.
     *
     * @param id       the ID of the ShiftDto to update
     * @param shiftDto the updated ShiftDto
     * @return the updated ShiftDto
     */
    ShiftDto update(long id, ShiftDto shiftDto);

    /**
     * Publishes all shifts within a specified date range.
     *
     * @param start the start date of the date range
     * @param end   the end date of the date range
     * @return a list of ShiftDto objects that were published
     */
    List<ShiftDto> publish(LocalDate start, LocalDate end);

    /**
     * Deletes a ShiftDto by its ID.
     *
     * @param id the ID of the ShiftDto to delete
     */
    void delete(long id);
}
