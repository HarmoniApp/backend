// SchedulePdfExportTest.java
package org.harmoniapp.services.importexport;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.FileGenerationException;
import org.harmoniapp.exception.InvalidDateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchedulePdfExportTest {

    @Mock
    private ScheduleDataService scheduleDataService;

    @InjectMocks
    private SchedulePdfExport schedulePdfExport;

    @Test
    public void exportShiftsTest() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(6);
        Shift shift = mock(Shift.class);
        User user = mock(User.class);
        Role role = mock(Role.class);
        when(user.getEmployeeId()).thenReturn("EMP01");
        when(role.getName()).thenReturn("role");
        when(shift.getUser()).thenReturn(user);
        when(shift.getStart()).thenReturn(startDate.atTime(8,0,0));
        when(shift.getEnd()).thenReturn(endDate.atTime(16,0,0));
        when(shift.getRole()).thenReturn(role);
        when(scheduleDataService.getShifts(startDate, endDate)).thenReturn(List.of(shift));

        InputStreamResource result = schedulePdfExport.exportShifts(startDate, endDate);

        assertNotNull(result);
    }

    @Test
    public void validateDateTest() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(6);

        assertDoesNotThrow(() -> schedulePdfExport.validateDate(startDate, endDate));
    }

    @Test
    public void validateDateNullTest() {
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now();

        assertThrows(InvalidDateException.class, () -> schedulePdfExport.validateDate(startDate, endDate));
    }

    @Test
    public void validateDateStartDateAfterEndDateTest() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now();

        assertThrows(InvalidDateException.class, () -> schedulePdfExport.validateDate(startDate, endDate));
    }

    @Test
    public void validateDateRangeTest() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(8);

        assertThrows(InvalidDateException.class, () -> schedulePdfExport.validateDate(startDate, endDate));
    }

    @Test
    public void writeDocumentTest() {
        Document document = mock(Document.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(6);

        assertDoesNotThrow(() -> schedulePdfExport.writeDocument(document, out, startDate, endDate));
    }

    @Test
    public void writeDocumentExceptionTest() {
        Document document = mock(Document.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(6);
        doThrow(DocumentException.class).when(document).open();

        assertThrows(FileGenerationException.class, () -> schedulePdfExport.writeDocument(document, out, startDate, endDate));
    }
}