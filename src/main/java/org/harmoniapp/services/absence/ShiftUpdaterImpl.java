package org.harmoniapp.services.absence;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Service implementation for updating shifts.
 */
@Service
@RequiredArgsConstructor
public class ShiftUpdaterImpl implements ShiftUpdater {
    private final RepositoryCollector repositoryCollector;

    /**
     * Removes shifts that overlap with the absence period.
     *
     * @param absence the Absence entity
     */
    @Override
    @Transactional
    public void removeOverlappedShifts(Absence absence) {
        LocalDateTime startDateTime = absence.getStart().atStartOfDay();
        LocalDateTime endDateTime = absence.getEnd().atTime(LocalTime.MAX);
        List<Shift> overlappingShifts = repositoryCollector.getShifts()
                .findAllByDateRangeAndUserId(startDateTime, endDateTime, absence.getUser().getId());
        repositoryCollector.getShifts().deleteAll(overlappingShifts);
    }
}
