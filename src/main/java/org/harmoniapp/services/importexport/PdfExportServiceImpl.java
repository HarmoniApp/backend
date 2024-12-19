package org.harmoniapp.services.importexport;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


/**
 * Service implementation for generating PDF reports.
 */
@Service
@RequiredArgsConstructor
public class PdfExportServiceImpl implements PdfExportService {
    private final ExportUser userPdfExport;
    private final ExportSchedule schedulePdfExport;

    /**
     * Generates a PDF report for a specific week.
     *
     * @param startOfWeek the start date of the week
     * @return a ResponseEntity containing the generated PDF as an InputStreamResource
     */
    public ResponseEntity<InputStreamResource> generatePdfForWeek(LocalDate startOfWeek) {
        LocalDate end = startOfWeek.plusDays(6);
        return schedulePdfExport.exportShifts(startOfWeek, end);
    }

    /**
     * Generates a PDF report for all employees.
     *
     * @return a ResponseEntity containing the generated PDF as an InputStreamResource
     */
    public ResponseEntity<InputStreamResource> generatePdfForAllEmployees() {
        return userPdfExport.exportUsers();
    }
}
