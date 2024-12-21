package org.harmoniapp.services.importexport;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EmptyFileException;
import org.harmoniapp.exception.InvalidCellException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Service class for importing schedules from an Excel file.
 * Extends the ExcelImport class and implements the ImportSchedule interface.
 */
@Service
@RequiredArgsConstructor
public class ScheduleExcelImport extends ExcelImport implements ImportSchedule {
    private final RepositoryCollector repositoryCollector;

    /**
     * Imports a schedule from an Excel file.
     *
     * @param file the Excel file containing the schedule
     * @return a status message
     * @throws EmptyFileException   if no rows are found in the Excel file
     * @throws InvalidCellException if an invalid employee ID is found in the Excel file
     */
    public String importSchedule(MultipartFile file) {
        Sheet sheet = readSheet(file);
        List<User> users = repositoryCollector.getUsers().findAllByIsActiveTrue();

        Iterator<Row> rows = sheet.rowIterator();
        if (!rows.hasNext()) {
            throw new EmptyFileException("No rows found in the Excel file");
        }
        List<LocalDateTime> dateHeaders = extractHeaders(rows.next());
        List<Shift> shiftList = processShiftRows(rows, users, dateHeaders);
        repositoryCollector.getShifts().saveAll(shiftList);
        return "Schedule imported successfully";
    }

    /**
     * Processes the rows from the Excel sheet and creates shifts.
     *
     * @param rows        the iterator of rows to process
     * @param users       the list of active users
     * @param dateHeaders the list of date headers
     * @return a list of created shifts
     */
    private List<Shift> processShiftRows(Iterator<Row> rows, List<User> users, List<LocalDateTime> dateHeaders) {
        List<Shift> shiftList = new ArrayList<>();
        while (rows.hasNext()) {
            Row row = rows.next();
            Cell empCell = row.getCell(0);
            User user = getUser(users, empCell);
            processRow(row, dateHeaders, user, shiftList);
        }
        return shiftList;
    }

    /**
     * Retrieves a User object based on the employee ID found in the given cell.
     *
     * @param users the list of active users
     * @param cell  the cell containing the employee ID
     * @return the User object corresponding to the employee ID
     * @throws InvalidCellException if the employee ID is not found in the list of users
     */
    private User getUser(List<User> users, Cell cell) {
        String empId = getCellValueAsString(cell);
        return users.stream()
                .filter(u -> u.getEmployeeId().equals(empId))
                .findFirst()
                .orElseThrow(() -> new InvalidCellException("Invalid cell: "
                        + cell.getAddress().formatAsString() + " - invalid employee ID"));
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
            LocalDateTime[] workHours = parseWorkHours(cellValue, day, cell);
            Shift shift = createShift(user, workHours[0], workHours[1]);
            shiftList.add(shift);
        }
    }

    /**
     * Parses the work hours from a cell value and combines them with a given day.
     *
     * @param cellValue the cell value containing the work hours in the format HH:mm-HH:mm
     * @param day       the day to combine with the parsed work hours
     * @param cell      the cell containing the work hours, used for error reporting
     * @return an array containing the start and end times as LocalDateTime objects
     * @throws InvalidCellException if the work hours format is invalid
     */
    private LocalDateTime[] parseWorkHours(String cellValue, LocalDateTime day, Cell cell) {
        List<String> workHours = List.of(cellValue.split("-", 2));
        LocalDateTime start = parseTime(workHours.get(0), day, cell);
        LocalDateTime end = parseTime(workHours.get(1), day, cell);
        if (start.isAfter(end)) {
            end = end.plusDays(1);
        }
        return new LocalDateTime[]{start, end};
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
     * @param headerRow the row containing the headers
     * @return a list of headers extracted from the row
     * @throws EmptyFileException   if no headers are found in the Excel file
     * @throws InvalidCellException if the "employee id" column is not found or if a date cell has an invalid format
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
