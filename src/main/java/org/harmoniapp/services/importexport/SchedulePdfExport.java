package org.harmoniapp.services.importexport;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.exception.FileGenerationException;
import org.harmoniapp.exception.InvalidDateException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for exporting schedule data to a PDF file.
 */
//TODO: export only active users
@Service
@RequiredArgsConstructor
public class SchedulePdfExport implements ExportSchedule, PdfExport {
    private final ScheduleDataService scheduleDataService;
    private final List<String> headersCell = List.of("ID Pracownika", "Poniedzialek", "Wtorek", "Sroda",
            "Czwartek", "Piatek", "Sobota", "Niedziela");
    private final int maxDays = 7;

    /**
     * Exports shifts within a given date range to a PDF file.
     *
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @return a ResponseEntity containing the PDF file as an InputStreamResource
     */
    public ResponseEntity<InputStreamResource> exportShifts(LocalDate startDate, LocalDate endDate) {
        validateDate(startDate, endDate);
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeDocument(document, out, startDate, endDate);
        return createResponseEntity(out);
    }

    /**
     * Validates the given date range.
     *
     * @param startDate the start date to validate
     * @param endDate   the end date to validate
     * @throws InvalidDateException if the start date or end date is null,
     *                              if the start date is after the end date,
     *                              or if the date range is longer than 7 days
     */
    private void validateDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidDateException("Data początkowa i końcowa nie mogą być puste.");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateException("Data początkowa nie może być późniejsza niż data końcowa.");
        }
        if (ChronoUnit.DAYS.between(startDate, endDate) > maxDays) {
            throw new InvalidDateException("Zakres dat nie może być dłuższy niż 7 dni.");
        }
    }

    /**
     * Writes the PDF document with the given date range.
     *
     * @param document  the PDF document to write to
     * @param out       the output stream to write the document to
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @throws FileGenerationException if there is an error generating the PDF file
     */
    private void writeDocument(Document document, ByteArrayOutputStream out, LocalDate startDate, LocalDate endDate) {
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            addDateParagraph(document, startDate, endDate);
            addTable(document, startDate, endDate);
            document.close();
        } catch (DocumentException e) {
            throw new FileGenerationException("Nie udało się wygenerować pliku PDF. Spróbuj ponownie.");
        }
    }

    /**
     * Adds a date paragraph to the PDF document.
     *
     * @param document the document to add the paragraph to
     * @param start    the start date of the range
     * @param end      the end date of the range
     * @throws DocumentException if there is an error adding the paragraph
     */
    private void addDateParagraph(Document document, LocalDate start, LocalDate end) throws DocumentException {
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
        Paragraph datePara = new Paragraph("Data: %s - %s".formatted(start, end), dateFont);
        datePara.setAlignment(Element.ALIGN_CENTER);
        document.add(datePara);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds a table to the PDF document with shifts data.
     *
     * @param document  the PDF document to add the table to
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @throws DocumentException if there is an error adding the table
     */
    private void addTable(Document document, LocalDate startDate, LocalDate endDate) throws DocumentException {
        List<Shift> shifts = scheduleDataService.getShiftsTmp(startDate, endDate);
        Map<String, Map<DayOfWeek, Shift>> shiftsByUserAndDay = groupShiftsByUserAndDay(shifts);
        PdfPTable table = new PdfPTable(maxDays + 1);
        addTableHeader(headersCell, table);
        shiftsByUserAndDay.entrySet().forEach(e -> addTableRow(table, e));
        document.add(table);
    }

    /**
     * Groups shifts by user and day of the week.
     *
     * @param shifts a list of Shift objects
     * @return a map where the key is the user ID and the value is another map with days of the week and shifts
     */
    private Map<String, Map<DayOfWeek, Shift>> groupShiftsByUserAndDay(List<Shift> shifts) {
        return shifts.stream()
                .collect(Collectors.groupingBy(
                        shift -> shift.getUser().getEmployeeId(),
                        Collectors.toMap(
                                shift -> shift.getStart().toLocalDate().getDayOfWeek(),
                                shift -> shift,
                                (existing, replacement) -> existing
                        )
                ));
    }

    /**
     * Adds a row to the table for each user and their shifts.
     *
     * @param table     the table to add the row to
     * @param userEntry a map entry containing the user ID and their shifts
     */
    private void addTableRow(PdfPTable table, Map.Entry<String, Map<DayOfWeek, Shift>> userEntry) {
        addCell(table, userEntry.getKey());

        Map<DayOfWeek, Shift> shiftsByDay = userEntry.getValue();
        for (DayOfWeek day : DayOfWeek.values()) {
            Shift shift = shiftsByDay.get(day);
            String shiftInfo = (shift != null && shift.getStart().toLocalDate().getDayOfWeek() == day) ? createShiftCellContent(shift) : "";
            addCell(table, shiftInfo);
        }
    }

    /**
     * Adds a cell to the table.
     *
     * @param table   the table to add the cell to
     * @param content the content of the cell
     */
    private void addCell(PdfPTable table, String content) {
        PdfPCell userIdCell = new PdfPCell(new Phrase(content));
        userIdCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        userIdCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(userIdCell);
    }

    /**
     * Creates the content for a shift cell.
     *
     * @param shift the shift to create the content for
     * @return a string containing the shift start time, end time, and role name
     */
    private String createShiftCellContent(Shift shift) {
        return "%s - %s\n%s".formatted(shift.getStart().toLocalTime(), shift.getEnd().toLocalTime(), shift.getRole().getName());
    }
}
