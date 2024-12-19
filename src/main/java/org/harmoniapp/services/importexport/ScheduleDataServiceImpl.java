package org.harmoniapp.services.importexport;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.exception.EntityNotFound;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Service implementation for handling schedule data operations.
 */
@Service
@RequiredArgsConstructor
public class ScheduleDataServiceImpl implements ScheduleDataService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a list of shifts within the specified date range.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return a list of shifts within the specified date range
     * @throws EntityNotFound if no shifts are found within the specified date range
     */
    public List<Shift> getShifts(LocalDate startDate, LocalDate endDate) {
        List<Shift> shifts = repositoryCollector.getShifts()
                .findPublishedByDataRange(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

        if (shifts.isEmpty()) {
            throw new EntityNotFound("Nie znaleziono żadnych zmian w podanym zakresie dat.");
        }
        return shifts;
    }
}
