package org.harmoniapp.harmoniwebapi.services;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.ArchivedShift;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.ArchivedShiftDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

/**
 * Service class for managing archived shifts.
 * Provides methods to generate PDFs and retrieve archived shifts.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class ArchivedShiftService {

    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a list of all archived shifts.
     *
     * @return a list of ArchivedShiftDto containing details of all archived shifts
     */
    public List<ArchivedShiftDto> getAllArchivedShifts() {
        List<ArchivedShift> archivedShifts = repositoryCollector.getArchivedShifts().findAll();

        return archivedShifts.stream()
                .map(ArchivedShiftDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves a specific archived shift PDF by its ID.
     *
     * @param id the ID of the archived shift to retrieve
     * @return ResponseEntity containing the archived shift PDF as InputStreamResource
     */
    public ResponseEntity<InputStreamResource> getArchivedShift(long id) {
        ArchivedShift archivedShift = repositoryCollector.getArchivedShifts().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Archived Shift not found"));

        byte[] pdfData = archivedShift.getPdfData();
        ByteArrayInputStream bis = new ByteArrayInputStream(pdfData);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    /**
     * Archives the shifts from the previous week by generating a PDF and saving it to the database.
     */
    @Transactional
    public void archivePreviousWeekShifts() {
        LocalDate today = LocalDate.now();
        LocalDate startOfPreviousWeek = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate endOfPreviousWeek = startOfPreviousWeek.plusDays(6);

        List<Shift> shifts = repositoryCollector.getShifts()
                .findAllByDateRange(startOfPreviousWeek.atStartOfDay(), endOfPreviousWeek.atTime(23, 59, 59));

        if (!shifts.isEmpty()) {
            String fileTitle = startOfPreviousWeek + " - " + endOfPreviousWeek;
            byte[] pdfData = generatePdf(shifts, fileTitle);

            ArchivedShift archivedShift = new ArchivedShift();
            archivedShift.setFileTitle(fileTitle);
            archivedShift.setPdfData(pdfData);

            repositoryCollector.getArchivedShifts().save(archivedShift);
        }
    }

    /**
     * Generates a PDF report based on the provided list of shifts and date range.
     *
     * @param shifts    the list of shifts to include in the PDF
     * @param dateRange the date range represented in the report
     * @return a byte array representing the generated PDF
     */
    private byte[] generatePdf(List<Shift> shifts, String dateRange) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            Paragraph datePara = new Paragraph("Date Range: " + dateRange, dateFont);
            datePara.setAlignment(Element.ALIGN_CENTER);
            document.add(datePara);
            document.add(Chunk.NEWLINE);

            Map<String, Map<DayOfWeek, List<Shift>>> shiftsByUserAndDay = shifts.stream()
                    .collect(Collectors.groupingBy(
                            shift -> shift.getUser().getEmployeeId(),
                            Collectors.groupingBy(shift -> shift.getStart().toLocalDate().getDayOfWeek())
                    ));

            PdfPTable table = new PdfPTable(8);

            Stream.of("EMPLOYEE ID", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                Font headFont = FontFactory.getFont(FontFactory.HELVETICA);
                header.setBackgroundColor(Color.ORANGE);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setBorderWidth(2);
                header.setPhrase(new Phrase(headerTitle, headFont));
                table.addCell(header);
            });

            for (Map.Entry<String, Map<DayOfWeek, List<Shift>>> userEntry : shiftsByUserAndDay.entrySet()) {
                String userId = userEntry.getKey();
                Map<DayOfWeek, List<Shift>> shiftsByDay = userEntry.getValue();

                PdfPCell userIdCell = new PdfPCell(new Phrase(userId));
                userIdCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                userIdCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(userIdCell);

                for (DayOfWeek day : DayOfWeek.values()) {
                    StringBuilder shiftInfo = new StringBuilder();

                    List<Shift> shiftsForDay = shiftsByDay.getOrDefault(day, List.of());
                    for (Shift shift : shiftsForDay) {
                        if (shift.getStart().toLocalDate().getDayOfWeek() == day) {
                            shiftInfo.append(shift.getStart().toLocalTime()).append(" - ")
                                    .append(shift.getEnd().toLocalTime()).append(" ")
                                    .append(shift.getRole().getName()).append("\n");
                        }
                    }

                    PdfPCell shiftCell = new PdfPCell(new Phrase(shiftInfo.toString()));
                    shiftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    shiftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(shiftCell);
                }
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out.toByteArray();
    }

    /**
     * Schedules the archiving of shifts from the previous week to occur every Monday at midnight.
     */
    @Scheduled(cron = "0 0 0 * * MON")
    public void scheduleWeeklyShiftArchival() {
        archivePreviousWeekShifts();
    }

    /**
     * Generates a PDF report for shifts in a specific week based on the provided start date.
     *
     * @param startOfWeek the start date of the week for which the report is generated
     * @return ResponseEntity containing the generated PDF as InputStreamResource
     */
    public ResponseEntity<InputStreamResource> generatePdfForWeek(LocalDate startOfWeek) {
        LocalDate endDate = startOfWeek.plusDays(6);

        List<Shift> shifts = repositoryCollector.getShifts()
                .findAllByDateRange(startOfWeek.atStartOfDay(), endDate.atTime(23, 59, 59));

        if (shifts.isEmpty()) {
            throw new RuntimeException("No shifts found for the specified date range.");
        }

        String dateRange = startOfWeek + " - " + endDate;
        byte[] pdfData = generatePdf(shifts, dateRange);

        ByteArrayInputStream bis = new ByteArrayInputStream(pdfData);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
