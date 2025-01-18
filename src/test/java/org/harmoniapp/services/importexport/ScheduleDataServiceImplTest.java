// ScheduleDataServiceImplTest.java
package org.harmoniapp.services.importexport;

import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.schedule.ShiftRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleDataServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private ShiftRepository shiftRepository;

    @InjectMocks
    private ScheduleDataServiceImpl scheduleDataService;

    @Test
    public void getShiftsTest() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(6);
        List<Shift> shifts = List.of(mock(Shift.class));
        when(repositoryCollector.getShifts()).thenReturn(shiftRepository);
        when(shiftRepository.findPublishedByDataRange(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))).thenReturn(shifts);

        List<Shift> result = scheduleDataService.getShifts(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getShiftsEmptyTest() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(6);
        when(repositoryCollector.getShifts()).thenReturn(shiftRepository);
        when(shiftRepository.findPublishedByDataRange(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> scheduleDataService.getShifts(startDate, endDate));
    }
}