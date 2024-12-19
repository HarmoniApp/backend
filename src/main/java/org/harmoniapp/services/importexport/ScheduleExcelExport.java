package org.harmoniapp.services.importexport;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for exporting schedule data to an Excel file.
 * Extends the ExcelExport class and implements the ExportSchedule interface.
 */
@Service
@AllArgsConstructor
public class ScheduleExcelExport extends ExcelExport implements ExportSchedule {
    private final ScheduleDataService scheduleDataService;

    /**
     * Exports shifts within a given date range to an Excel file.
     *
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @return a ResponseEntity containing the Excel file as an InputStreamResource
     */
    @Override
    public ResponseEntity<InputStreamResource> exportShifts(LocalDate startDate, LocalDate endDate) {
        List<Shift> shifts = scheduleDataService.getShifts(startDate, endDate);
        List<User> users = getUsers(shifts);
        Map<Long, Map<LocalDate, String>> userShiftMap = createUserShiftMap(shifts);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = createSheetWithHeader(workbook, startDate, endDate);
        populateSheetWithData(sheet, users, userShiftMap, startDate, endDate);

        ByteArrayInputStream in = writeFile(workbook);
        return createResponse(in);
    }

    /**
     * Creates a map of user shifts.
     *
     * @param shifts a list of Shift objects
     * @return a map where the key is the user ID and the value is another map
     * with dates as keys and shift times as values
     */
    private Map<Long, Map<LocalDate, String>> createUserShiftMap(List<Shift> shifts) {
        Map<Long, Map<LocalDate, String>> userShiftMap = new HashMap<>();
        for (Shift shift : shifts) {
            userShiftMap.computeIfAbsent(shift.getUser().getId(), k -> new HashMap<>())
                    .put(shift.getStart().toLocalDate(), shift.getStart().toLocalTime() + " - " + shift.getEnd().toLocalTime());
        }
        return userShiftMap;
    }

    /**
     * Creates an Excel sheet with a header row.
     *
     * @param workbook  the workbook to create the sheet in
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @return the created sheet
     */
    private Sheet createSheetWithHeader(Workbook workbook, LocalDate startDate, LocalDate endDate) {
        Sheet sheet = workbook.createSheet("Grafik");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID Pracownika");

        LocalDate current = startDate;
        int colIdx = 1;
        while (!current.isAfter(endDate)) {
            headerRow.createCell(colIdx++).setCellValue(current.toString());
            current = current.plusDays(1);
        }
        return sheet;
    }

    /**
     * Populates the Excel sheet with user shift data.
     *
     * @param sheet        the sheet to populate
     * @param users        the list of users
     * @param userShiftMap the map of user shifts
     * @param startDate    the start date of the range
     * @param endDate      the end date of the range
     */
    private void populateSheetWithData(Sheet sheet, List<User> users, Map<Long, Map<LocalDate, String>> userShiftMap,
                                       LocalDate startDate, LocalDate endDate) {
        int rowIdx = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(user.getEmployeeId());
            Map<LocalDate, String> shiftsForUser = userShiftMap.getOrDefault(user.getId(), new HashMap<>());
            populateRowWithUserData(row, shiftsForUser, startDate, endDate);
        }

        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Populates a row with user shift data for the given date range.
     *
     * @param row           the row to populate
     * @param shiftsForUser a map of dates to shift times for the user
     * @param startDate     the start date of the range
     * @param endDate       the end date of the range
     */
    private void populateRowWithUserData(Row row, Map<LocalDate, String> shiftsForUser,
                                         LocalDate startDate, LocalDate endDate) {
        LocalDate current = startDate;
        int colIdx = 1;
        while (!current.isAfter(endDate)) {
            String shiftTime = shiftsForUser.get(current);
            row.createCell(colIdx).setCellValue(shiftTime != null ? shiftTime : "");
            colIdx++;
            current = current.plusDays(1);
        }
    }
}
