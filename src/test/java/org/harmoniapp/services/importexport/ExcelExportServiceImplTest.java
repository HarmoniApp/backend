// ExcelExportServiceImplTest.java
package org.harmoniapp.services.importexport;

import org.harmoniapp.exception.InvalidDateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcelExportServiceImplTest {

    @Mock
    private ExportUser userExcelExport;

    @Mock
    private ExportSchedule scheduleExcelExport;

    @InjectMocks
    private ExcelExportServiceImpl excelExportService;

    @Test
    public void exportUsersTest() {
        InputStreamResource resource = mock(InputStreamResource.class);
        when(userExcelExport.exportUsers()).thenReturn(resource);

        InputStreamResource result = excelExportService.exportUsers();

        assertNotNull(result);
        assertEquals(resource, result);
    }

    @Test
    public void exportShiftsTest() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(6);
        InputStreamResource resource = mock(InputStreamResource.class);
        when(scheduleExcelExport.exportShifts(startDate, endDate)).thenReturn(resource);

        InputStreamResource result = excelExportService.exportShifts(startDate.toString(), endDate.toString());

        assertNotNull(result);
        assertEquals(resource, result);
    }

    @Test
    public void exportShiftsInvalidDateTest() {
        assertThrows(InvalidDateException.class, () -> excelExportService.exportShifts("invalid-date", "invalid-date"));
    }
}