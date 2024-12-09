package org.harmoniapp.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PartialAbsenceDto;
import org.harmoniapp.contracts.ShiftDto;
import org.harmoniapp.contracts.UserScheduleDto;
import org.harmoniapp.entities.Absence;
import org.harmoniapp.entities.Shift;
import org.harmoniapp.entities.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing  user's schedule.
 * Provides methods to get the weekly schedule for a user,
 * including their shifts and absences for a specified date range.
 */
@Service
@RequiredArgsConstructor
public class UserScheduleService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves the weekly schedule (shifts and absences) for a specific user within a given date range.
     *
     * @param userId    the ID of the user for whom the schedule is being retrieved
     * @param startDate the start date of the week (as LocalDateTime) to filter shifts and absences
     * @param endDate   the end date of the week (as LocalDateTime) to filter shifts and absences
     * @param published an optional boolean to filter shifts; if true, only published shifts are returned
     * @return a UserScheduleDto containing the user's details along with their shifts and absences for the specified week
     * @throws RuntimeException if the user is not found in the repository
     */
    public UserScheduleDto getUserWeeklySchedule(Long userId, LocalDateTime startDate, LocalDateTime endDate, boolean published) {
        User user = repositoryCollector.getUsers().findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Shift> shifts;
        if (published) {
            shifts = repositoryCollector.getShifts().findPublishedByDateRangeAndUserId(startDate, endDate, userId);
        } else {
            shifts = repositoryCollector.getShifts().findAllByDateRangeAndUserId(startDate, endDate, userId);
        }

        List<Absence> absences = repositoryCollector.getAbsences().findApprovedAbsenceByDateRangeAndUserId(startDate.toLocalDate(), endDate.toLocalDate(), userId);

        List<ShiftDto> shiftDto = shifts.stream().map(ShiftDto::fromEntity).toList();
        List<PartialAbsenceDto> partialAbsenceDto = absences.stream()
                .map(PartialAbsenceDto::fromEntity)
                .toList();

        return new UserScheduleDto(userId, shiftDto, partialAbsenceDto);
    }
}
