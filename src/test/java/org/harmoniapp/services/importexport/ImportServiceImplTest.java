// ImportServiceImplTest.java
package org.harmoniapp.services.importexport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImportServiceImplTest {

    @Mock
    private ImportUser userExcelImport;

    @Mock
    private ImportSchedule scheduleExcelImport;

    @InjectMocks
    private ImportServiceImpl importService;

    @Test
    public void importUsersFromExcelTest() {
        MultipartFile file = mock(MultipartFile.class);
        InputStreamResource resource = mock(InputStreamResource.class);
        when(userExcelImport.importUsers(file)).thenReturn(resource);

        InputStreamResource result = importService.importUsersFromExcel(file);

        assertNotNull(result);
        assertEquals(resource, result);
    }

    @Test
    public void importScheduleFromExcelTest() {
        MultipartFile file = mock(MultipartFile.class);
        String statusMessage = "Import successful";
        when(scheduleExcelImport.importSchedule(file)).thenReturn(statusMessage);

        String result = importService.importScheduleFromExcel(file);

        assertNotNull(result);
        assertEquals(statusMessage, result);
    }
}