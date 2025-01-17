package org.harmoniapp.services.importexport;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScheduleExcelExportTest {

    private ScheduleExcelExport scheduleExcelExport;

    @Mock
    private ScheduleDataServiceImpl scheduleDataService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduleExcelExport = new ScheduleExcelExport(scheduleDataService);
    }

    @Test
    public void exportShiftsTest() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 7);

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmployeeId()).thenReturn("123");
        Shift shift = mock(Shift.class);
        when(shift.getUser()).thenReturn(user);
        when(shift.getStart()).thenReturn(startDate.atTime(8, 0));
        when(shift.getEnd()).thenReturn(endDate.atTime(16, 0));
        List<Shift> shifts = List.of(shift);
        when(scheduleDataService.getShifts(startDate, endDate)).thenReturn(shifts);

        InputStreamResource resource = scheduleExcelExport.exportShifts(startDate, endDate);

        assertNotNull(resource);
    }

    @Test
    public void createUserShiftMapTest() {
        Shift shift = mock(Shift.class);
        User user = mock(User.class);
        when(shift.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(shift.getStart()).thenReturn(LocalDate.of(2023, 1, 1).atStartOfDay());
        when(shift.getEnd()).thenReturn(LocalDate.of(2023, 1, 1).atTime(8, 0));

        Map<Long, Map<LocalDate, String>> userShiftMap = scheduleExcelExport.createUserShiftMap(List.of(shift));

        assertNotNull(userShiftMap);
        assertTrue(userShiftMap.containsKey(1L));
    }

    @Test
    public void createSheetWithHeaderTest() {
        Workbook workbook = new XSSFWorkbook();
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 7);

        Sheet sheet = scheduleExcelExport.createSheetWithHeader(workbook, startDate, endDate);

        assertNotNull(sheet);
        assertEquals("ID Pracownika", sheet.getRow(0).getCell(0).getStringCellValue());
    }

    @Test
    public void populateRowWithUserDataTest() {
        Sheet sheet = new XSSFWorkbook().createSheet();
        Row row = sheet.createRow(0);
        Map<LocalDate, String> shiftsForUser = Map.of(LocalDate.of(2023, 1, 1), "08:00 - 16:00");

        scheduleExcelExport.populateRowWithUserData(row, shiftsForUser, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 7));

        assertEquals("08:00 - 16:00", row.getCell(1).getStringCellValue());
    }

    @Test
    public void populateSheetWithDataTest() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        User user1 = mock(User.class);
        when(user1.getEmployeeId()).thenReturn("EMP001");
        when(user1.getId()).thenReturn(1L);
        User user2 = mock(User.class);
        when(user2.getEmployeeId()).thenReturn("EMP002");
        when(user2.getId()).thenReturn(2L);
        List<User> users = List.of(user1, user2);

        Map<Long, Map<LocalDate, String>> userShiftMap = new HashMap<>();
        userShiftMap.put(1L, Map.of(
                LocalDate.of(2023, 1, 1), "Role A",
                LocalDate.of(2023, 1, 2), "Role B"
        ));

        userShiftMap.put(2L, Map.of(
                LocalDate.of(2023, 1, 1), "Role C"
        ));

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 2);

        scheduleExcelExport.populateSheetWithData(sheet, users, userShiftMap, startDate, endDate);

        assertEquals(users.size(), sheet.getPhysicalNumberOfRows()); // Excluding header row

        Row firstUserRow = sheet.getRow(1); // Row for the first user
        assertEquals("EMP001", firstUserRow.getCell(0).getStringCellValue());

        Row secondUserRow = sheet.getRow(2); // Row for the second user
        assertEquals("EMP002", secondUserRow.getCell(0).getStringCellValue());
    }
}