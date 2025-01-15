package org.harmoniapp.services.schedule;

import org.harmoniapp.contracts.schedule.ScheduleRequestDto;
import org.harmoniapp.contracts.schedule.UserScheduleDto;
import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.exception.InvalidDateException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.absence.AbsenceRepository;
import org.harmoniapp.repositories.schedule.ShiftRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private AbsenceRepository absenceRepository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @BeforeEach
    public void setUp() {
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
    }

    @Test
    public void getUserWeeklyScheduleTest() {
        Long userId = 1L;
        ScheduleRequestDto scheduleRequestDto = new ScheduleRequestDto(LocalDateTime.now(), LocalDateTime.now().plusDays(7), true);
        Shift shift = new Shift();
        Absence absence = new Absence();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(repositoryCollector.getShifts()).thenReturn(shiftRepository);
        when(shiftRepository.findPublishedByDateRangeAndUserId(any(LocalDateTime.class), any(LocalDateTime.class), eq(userId))).thenReturn(List.of(shift));
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(absenceRepository.findApprovedAbsenceByDateRangeAndUserId(any(LocalDate.class), any(LocalDate.class), eq(userId))).thenReturn(List.of(absence));

        UserScheduleDto result = scheduleService.getUserWeeklySchedule(userId, scheduleRequestDto);

        assertNotNull(result);
        assertEquals(1, result.shifts().size());
        assertEquals(1, result.absences().size());
    }

    @Test
    public void getUserWeeklyScheduleInvalidUserTest() {
        Long userId = 1L;
        ScheduleRequestDto scheduleRequestDto = new ScheduleRequestDto(LocalDateTime.now(), LocalDateTime.now().plusDays(7), true);
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> scheduleService.getUserWeeklySchedule(userId, scheduleRequestDto));
    }

    @Test
    public void getUserWeeklyScheduleInvalidDateTest() {
        Long userId = 1L;
        ScheduleRequestDto scheduleRequestDto = new ScheduleRequestDto(null, null, true);
        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(InvalidDateException.class, () -> scheduleService.getUserWeeklySchedule(userId, scheduleRequestDto));
    }
}