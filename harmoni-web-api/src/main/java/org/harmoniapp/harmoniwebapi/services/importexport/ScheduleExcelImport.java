package org.harmoniapp.harmoniwebapi.services.importexport;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.exception.InvalidCellException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Service for importing schedules from an Excel file.
 */
@Component
@RequiredArgsConstructor
public class ScheduleExcelImport implements ImportSchedule, ReadWorkbook {
    private final RepositoryCollector repositoryCollector;

    /**
     * Imports a schedule from an Excel file.
     *
     * @param file the Excel file containing the schedule.
     * @return a ResponseEntity with a success message.
     */
    public ResponseEntity<String> importSchedule(MultipartFile file) {
        Sheet sheet = readSheet(file);
        List<User> users = repositoryCollector.getUsers().findAllByIsActive(true);

        Iterator<Row> rows = sheet.rowIterator();
        if (!rows.hasNext()) {
            throw new IllegalArgumentException("No rows found in the Excel file");
        }
        List<String> header = extractHeaders(rows.next());
        List<Shift> shiftList = new ArrayList<>();
        while (rows.hasNext()) {
            Row row = rows.next();
            String employeeId = row.getCell(header.indexOf("employee id")).getStringCellValue();
            processRow(row, header, users, shiftList, employeeId);
        }
        repositoryCollector.getShifts().saveAll(shiftList);
        return ResponseEntity.ok("Schedule imported successfully");

    }

    /**
     * Processes a row from the Excel sheet and creates shifts.
     *
     * @param row the row to process.
     * @param header the list of headers from the Excel sheet.
     * @param users the list of active users.
     * @param shiftList the list to add created shifts to.
     * @param employeeId the employee ID of the user.
     */
    private void processRow(Row row, List<String> header, List<User> users, List<Shift> shiftList, String employeeId) {
        for (int i = 1; i < header.size(); i++) {
            Cell cell = row.getCell(i);
            cell.setCellType(CellType.STRING);
            String cellValue = cell.getStringCellValue().trim();
            if (cellValue.isEmpty()) {
                continue;
            }
            LocalDateTime day = LocalDate.parse(header.get(i)).atStartOfDay();
            List<String> workHours = List.of(cellValue.split("-", 2));
            LocalDateTime start = parseTime(workHours.get(0), day, cell);
            LocalDateTime end = parseTime(workHours.get(1), day, cell);
            if (start.isAfter(end)) {
                end = end.plusDays(1);
            }
            Shift shift = createShift(users, employeeId, start, end);
            shiftList.add(shift);
        }
    }

    /**
     * Parses a time string and combines it with a given day to create a LocalDateTime.
     *
     * @param time the time string to parse, expected format: HH:mm
     * @param day the day to combine with the parsed time
     * @param cell the cell containing the time string, used for error reporting
     * @return a LocalDateTime combining the given day and parsed time
     * @throws InvalidCellException if the time string is invalid
     */
    private LocalDateTime parseTime(String time, LocalDateTime day, Cell cell) {
        try {
            LocalTime localTime = LocalTime.parse(time.trim());
            return day.plusHours(localTime.getHour()).plusMinutes(localTime.getMinute());
        } catch (Exception e) {
            throw new InvalidCellException("Invalid cell: " + cell.getAddress().formatAsString() + " - expected format: HH:mm-HH:mm");
        }
    }

    /**
     * Creates a Shift object for a given user and time range.
     *
     * @param users the list of active users.
     * @param employeeId the employee ID of the user.
     * @param start the start time of the shift.
     * @param end the end time of the shift.
     * @return a Shift object for the given user and time range.
     * @throws IllegalArgumentException if the user is not found.
     */
    private Shift createShift(List<User> users, String employeeId, LocalDateTime start, LocalDateTime end) {
        User user = users.stream()
                .filter(u -> u.getEmployeeId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return Shift.builder()
                .user(user)
                .start(start)
                .end(end)
                .published(false)
                .build();
    }

    /**
     * Extracts headers from the given header row.
     *
     * @param headerRow the row containing the headers.
     * @return a list of headers extracted from the row.
     * @throws IllegalArgumentException if the "employee id" column is not found.
     */
    private List<String> extractHeaders(Row headerRow) {
        List<String> headers = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        short dataFormat = headerRow.getSheet().getWorkbook().getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd");
        for (Cell cell : headerRow) {
            if (cell.getCellType() == CellType.STRING) {
                headers.add(cell.getStringCellValue().toLowerCase());
            } else {
                cell.getCellStyle().setDataFormat(dataFormat);
                headers.add(dataFormatter.formatCellValue(cell));
            }
        }
        if (!headers.getFirst().equals("employee id")) {
            throw new IllegalArgumentException("Employee ID column not found in the Excel file");
        }
        return headers;
    }
}
