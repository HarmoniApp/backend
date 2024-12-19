package org.harmoniapp.services.importexport;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.schedule.ShiftDto;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.exception.EntityNotFound;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleDataServiceImpl implements ScheduleDataService {
    private final RepositoryCollector repositoryCollector;

    public List<ShiftDto> getShifts(LocalDate startDate, LocalDate endDate) {
        List<ShiftDto> shifts = repositoryCollector.getShifts()
                .findAllByDateRangeAndPublishedIsTrue(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))
                .stream()
                .map(ShiftDto::fromEntity)
                .toList();

        if (shifts.isEmpty()) {
            throw new EntityNotFound("Nie znaleziono żadnych zmian w podanym zakresie dat.");
        }
        return shifts;
    }

    //TODO: remove
    public List<Shift> getShiftsTmp(LocalDate startDate, LocalDate endDate) {
        List<Shift> shifts = repositoryCollector.getShifts()
                .findAllByDateRangeAndPublishedIsTrue(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

        if (shifts.isEmpty()) {
            throw new EntityNotFound("Nie znaleziono żadnych zmian w podanym zakresie dat.");
        }
        return shifts;
    }
}
