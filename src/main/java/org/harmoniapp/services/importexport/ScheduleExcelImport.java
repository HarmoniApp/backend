package org.harmoniapp.services.importexport;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.exception.EmptyFileException;
import org.harmoniapp.exception.InvalidCellException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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
     * @throws EmptyFileException if no rows are found in the Excel file.
     * @throws InvalidCellException if an invalid employee ID is found in the Excel file.
     */
    public ResponseEntity<String> importSchedule(MultipartFile file) {
        Sheet sheet = readSheet(file);
        List<User> users = repositoryCollector.getUsers().findAllByIsActive(true);

        Iterator<Row> rows = sheet.rowIterator();
        if (!rows.hasNext()) {
            throw new EmptyFileException("No rows found in the Excel file");
        }
        List<LocalDateTime> dateHeaders = extractHeaders(rows.next());
        List<Shift> shiftList = new ArrayList<>();
        while (rows.hasNext()) {
            Row row = rows.next();

            Cell empCell = row.getCell(0);
            String empId = getCellValueAsString(empCell);
            User user = users.stream()
                    .filter(u -> u.getEmployeeId().equals(empId))
                    .findFirst()
                    .orElseThrow(() -> new InvalidCellException("Invalid cell: "
                            + empCell.getAddress().formatAsString() + " - invalid employee ID"));

            processRow(row, dateHeaders, user, shiftList);
        }
        repositoryCollector.getShifts().saveAll(shiftList);
        return ResponseEntity.ok("Schedule imported successfully");

    }

    /**
     * Processes a row from the Excel sheet and creates shifts for the user.
     *
     * @param row       the row to process
     * @param header    the list of date headers
     * @param user      the user associated with the row
     * @param shiftList the list to add the created shifts to
     */
    private void processRow(Row row, List<LocalDateTime> header, User user, List<Shift> shiftList) {
        for (int i = 0; i < header.size(); i++) {
            Cell cell = row.getCell(i + 1);
            String cellValue = getCellValueAsString(cell);
            if (cellValue.isEmpty()) {
                continue;
            }

            LocalDateTime day = header.get(i);
            List<String> workHours = List.of(cellValue.split("-", 2));
            LocalDateTime start = parseTime(workHours.get(0), day, cell);
            LocalDateTime end = parseTime(workHours.get(1), day, cell);
            if (start.isAfter(end)) {
                end = end.plusDays(1);
            }

            Shift shift = createShift(user, start, end);
            shiftList.add(shift);
        }
    }

    /**
     * Converts the value of a cell to a string.
     *
     * @param cell the cell to convert
     * @return the string value of the cell
     */
    private String getCellValueAsString(Cell cell) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    /**
     * Creates a new Shift object.
     *
     * @param user  the user associated with the shift
     * @param start the start time of the shift
     * @param end   the end time of the shift
     * @return a new Shift object
     */
    private Shift createShift(User user, LocalDateTime start, LocalDateTime end) {
        return Shift.builder()
                .user(user)
                .start(start)
                .end(end)
                .published(false)
                .build();
    }

    /**
     * Parses a time string and combines it with a given day to create a LocalDateTime.
     *
     * @param time the time string to parse, expected format: HH:mm
     * @param day  the day to combine with the parsed time
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
     * Extracts headers from the given header row.
     *
     * @param headerRow the row containing the headers.
     * @return a list of headers extracted from the row.
     * @throws EmptyFileException if no headers are found in the Excel file.
     * @throws InvalidCellException if the "employee id" column is not found or if a date cell has an invalid format.
     */
    private List<LocalDateTime> extractHeaders(Row headerRow) {
        List<LocalDateTime> headers = new ArrayList<>();
        short dataFormat = headerRow.getSheet().getWorkbook().getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd");

        Iterator<Cell> cellIterator = headerRow.cellIterator();
        if (!cellIterator.hasNext()) {
            throw new EmptyFileException("No headers found in the Excel file");
        }
        Cell cell = cellIterator.next();
        if (!cell.getStringCellValue().equalsIgnoreCase("employee id")) {
            throw new InvalidCellException("Invalid cell: " + cell.getAddress().formatAsString()
                    + " - expected header: Employee ID");
        }
        while (cellIterator.hasNext()) {
            cell = cellIterator.next();
            cell.getCellStyle().setDataFormat(dataFormat);
            try {
                LocalDateTime date = cell.getLocalDateTimeCellValue();
                headers.add(date);
            } catch (IllegalStateException e) {
                throw new InvalidCellException("Invalid cell: " + cell.getAddress().formatAsString() + " - expected date format");
            }
        }
        return headers;
    }
}
