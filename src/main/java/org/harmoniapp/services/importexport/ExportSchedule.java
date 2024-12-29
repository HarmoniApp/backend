package org.harmoniapp.services.importexport;

import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.springframework.core.io.InputStreamResource;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for exporting schedules.
 */
public interface ExportSchedule {

    /**
     * Exports shifts within the specified date range.
     *
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @return an InputStreamResource containing the exported data
     */
    InputStreamResource exportShifts(LocalDate startDate, LocalDate endDate);


    /**
     * Retrieves a list of users from the given list of shifts.
     *
     * @param shifts a list of Shift objects
     * @return a list of active User objects
     */
    default List<User> getUsers(List<Shift> shifts) {
        return shifts.stream().map(Shift::getUser)
                .distinct()
                .toList();
    }
}
