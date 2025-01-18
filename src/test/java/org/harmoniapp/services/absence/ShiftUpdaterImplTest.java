package org.harmoniapp.services.absence;

import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.schedule.ShiftRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShiftUpdaterImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private ShiftRepository shiftRepository;

    @InjectMocks
    private ShiftUpdaterImpl shiftUpdater;

    @Test
    public void removeOverlappedShiftsTest() {
        Absence absence = mock(Absence.class);
        User user = mock(User.class);
        when(absence.getStart()).thenReturn(LocalDate.of(2023, 1, 1));
        when(absence.getEnd()).thenReturn(LocalDate.of(2023, 1, 10));
        when(absence.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(repositoryCollector.getShifts()).thenReturn(shiftRepository);

        List<Shift> overlappingShifts = List.of(mock(Shift.class), mock(Shift.class));
        LocalDateTime startDateTime = LocalDate.of(2023, 1, 1).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.of(2023, 1, 10).atTime(LocalTime.MAX);
        when(shiftRepository.findAllByDateRangeAndUserId(startDateTime, endDateTime, 1L)).thenReturn(overlappingShifts);

        shiftUpdater.removeOverlappedShifts(absence);

        verify(shiftRepository).deleteAll(overlappingShifts);
    }
}