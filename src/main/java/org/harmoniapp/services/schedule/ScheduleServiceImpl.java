package org.harmoniapp.services.schedule;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.absence.PartialAbsenceDto;
import org.harmoniapp.contracts.schedule.ScheduleRequestDto;
import org.harmoniapp.contracts.schedule.ShiftDto;
import org.harmoniapp.contracts.schedule.UserScheduleDto;
import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.exception.InvalidDateException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the ScheduleService interface.
 * This service provides methods to manage user schedules.
 */
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves the weekly schedule for a user within a specified date range.
     *
     * @param userId             the ID of the user
     * @param scheduleRequestDto the schedule request data transfer object containing the date range and publication status
     * @return a UserScheduleDto containing the user's shifts and absences
     * @throws EntityNotFoundException if the user ID is invalid
     */
    public UserScheduleDto getUserWeeklySchedule(Long userId, ScheduleRequestDto scheduleRequestDto) {
        validateUserId(userId);
        validateDate(scheduleRequestDto);

        List<Shift> shifts = getShiftsForUser(userId, scheduleRequestDto);
        List<Absence> absences = getAbsencesForUser(userId, scheduleRequestDto.startDate(), scheduleRequestDto.endDate());

        List<ShiftDto> shiftDto = shifts.stream().map(ShiftDto::fromEntity).toList();
        List<PartialAbsenceDto> partialAbsenceDto = absences.stream().map(PartialAbsenceDto::fromEntity).toList();

        return new UserScheduleDto(userId, shiftDto, partialAbsenceDto);
    }

    /**
     * Validates the user ID.
     *
     * @param userId the ID of the user to validate
     * @throws EntityNotFoundException if the user ID is null or does not exist
     */
    private void validateUserId(Long userId) {
        if (userId == null || !repositoryCollector.getUsers().existsById(userId)) {
            throw new EntityNotFoundException("Nie znaleziono użytkownika o ID %d".formatted(userId));
        }
    }

    /**
     * Validates the date range in the schedule request.
     *
     * @param scheduleRequestDto the schedule request data transfer object containing the date range
     * @throws InvalidDateException if the start date or end date is null
     */
    private void validateDate(ScheduleRequestDto scheduleRequestDto) {
        if (scheduleRequestDto.startDate() == null || scheduleRequestDto.endDate() == null) {
            throw new InvalidDateException("Data początkowa i końcowa są wymagane");
        }
    }

    /**
     * Retrieves the shifts for a user within a specified date range.
     *
     * @param userId             the ID of the user
     * @param scheduleRequestDto the schedule request data transfer object containing the date range and publication status
     * @return a list of shifts for the user
     */
    private List<Shift> getShiftsForUser(Long userId, ScheduleRequestDto scheduleRequestDto) {
        if (scheduleRequestDto.published()) {
            return repositoryCollector.getShifts()
                    .findPublishedByDateRangeAndUserId(scheduleRequestDto.startDate(), scheduleRequestDto.endDate(), userId);
        } else {
            return repositoryCollector.getShifts()
                    .findAllByDateRangeAndUserId(scheduleRequestDto.startDate(), scheduleRequestDto.endDate(), userId);
        }
    }

    /**
     * Retrieves the approved absences for a user within a specified date range.
     *
     * @param userId    the ID of the user
     * @param startDate the start date of the absences
     * @param endDate   the end date of the absences
     * @return a list of approved absences for the user
     */
    private List<Absence> getAbsencesForUser(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return repositoryCollector.getAbsences().
                findApprovedAbsenceByDateRangeAndUserId(startDate.toLocalDate(), endDate.toLocalDate(), userId);
    }
}
