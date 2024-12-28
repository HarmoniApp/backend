package org.harmoniapp.services.importexport;

import org.harmoniapp.entities.schedule.Shift;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for handling schedule data operations.
 */
public interface ScheduleDataService {

    /**
     * Retrieves a list of shifts within the specified date range.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return a list of shifts within the specified date range
     */
    List<Shift> getShifts(LocalDate startDate, LocalDate endDate);
}
