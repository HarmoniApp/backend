package org.harmoniapp.services.schedule;

import org.harmoniapp.contracts.schedule.ShiftDto;
import org.harmoniapp.services.CrudService;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing shifts.
 * Extends the CrudService interface for ShiftDto.
 */
public interface ShiftService extends CrudService<ShiftDto> {

    /**
     * Retrieves all shifts within a specified date range.
     *
     * @param start the start date of the date range
     * @param end   the end date of the date range
     * @return a list of ShiftDto objects within the specified date range
     */
    List<ShiftDto> getShiftsByDateRangeAndUserId(String start, String end, Long userId);

    /**
     * Publishes all shifts within a specified date range.
     *
     * @param start the start date of the date range
     * @param end   the end date of the date range
     * @return a list of ShiftDto objects that were published
     */
    List<ShiftDto> publish(LocalDate start, LocalDate end);
}
