package org.harmoniapp.services.importexport;

import org.apache.poi.ss.usermodel.*;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleExcelImportTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ScheduleExcelImport scheduleExcelImport;

    @Test
    public void processShiftRowsTest() {
        Iterator<Row> rows = mock(Iterator.class);
        List<User> users = List.of(mock(User.class));
        List<LocalDateTime> dateHeaders = List.of(LocalDateTime.now());
        when(rows.hasNext()).thenReturn(true, false);
        when(rows.next()).thenReturn(mock(Row.class));

        List<Shift> result = scheduleExcelImport.processShiftRows(rows, users, dateHeaders);

        assertNotNull(result);
    }

    @Test
    public void getUserTest() {
        List<User> users = List.of(mock(User.class));
        Cell cell = mock(Cell.class);
        when(cell.getStringCellValue()).thenReturn("123");
        when(users.getFirst().getEmployeeId()).thenReturn("123");

        User result = scheduleExcelImport.getUser(users, cell);

        assertNotNull(result);
        assertEquals("123", result.getEmployeeId());
    }

    @Test
    public void processRowEmptyCellTest() {
        Row row = mock(Row.class);
        List<LocalDateTime> header = List.of(LocalDateTime.now());
        User user = mock(User.class);
        List<Shift> shiftList = mock(List.class);

        assertDoesNotThrow(() -> scheduleExcelImport.processRow(row, header, user, shiftList));
    }

    @Test
    public void processRowTest() {
        Row row = mock(Row.class);
        List<LocalDateTime> header = List.of(LocalDateTime.now());
        User user = mock(User.class);
        List<Shift> shiftList = new ArrayList<>();
        Cell cell = mock(Cell.class);
        when(row.getCell(anyInt())).thenReturn(cell);
        when(cell.getStringCellValue()).thenReturn("08:00-16:00");

        scheduleExcelImport.processRow(row, header, user, shiftList);

        assertFalse(shiftList.isEmpty());
        assertEquals(1, shiftList.size());
        Shift shift = shiftList.get(0);
        assertEquals(user, shift.getUser());
        assertEquals(header.get(0).toLocalDate().atTime(8, 0), shift.getStart());
        assertEquals(header.get(0).toLocalDate().atTime(16, 0), shift.getEnd());
    }

    @Test
    public void extractHeadersTest() {
        Row headerRow = mock(Row.class);
        Sheet sheet = mock(Sheet.class);
        Workbook workbook = mock(Workbook.class);
        CreationHelper creationHelper = mock(CreationHelper.class);
        DataFormat dataFormat = mock(DataFormat.class);
        Cell cell1 = mock(Cell.class);
        Cell cell2 = mock(Cell.class);
        CellStyle cellStyle = mock(CellStyle.class);
        Iterator<Cell> cellIterator = List.of(cell1, cell2).iterator();
        when(headerRow.cellIterator()).thenReturn(cellIterator);
        when(cell1.getStringCellValue()).thenReturn("id pracownika");
        when(cell2.getCellStyle()).thenReturn(cellStyle);
        when(headerRow.getSheet()).thenReturn(sheet);
        when(sheet.getWorkbook()).thenReturn(workbook);
        when(workbook.getCreationHelper()).thenReturn(creationHelper);
        when(creationHelper.createDataFormat()).thenReturn(dataFormat);
        when(dataFormat.getFormat("yyyy-mm-dd")).thenReturn((short) 14);

        List<LocalDateTime> result = scheduleExcelImport.extractHeaders(headerRow);

        assertNotNull(result);
    }
}