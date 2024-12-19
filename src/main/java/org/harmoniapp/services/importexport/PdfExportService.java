package org.harmoniapp.services.importexport;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

/**
 * Service interface for generating PDF reports.
 */
public interface PdfExportService {

    /**
     * Generates a PDF report for a specific week.
     *
     * @param startOfWeek the start date of the week
     * @return a ResponseEntity containing the generated PDF as an InputStreamResource
     */
    ResponseEntity<InputStreamResource> generatePdfForWeek(LocalDate startOfWeek);

    /**
     * Generates a PDF report for all employees.
     *
     * @return a ResponseEntity containing the generated PDF as an InputStreamResource
     */
    ResponseEntity<InputStreamResource> generatePdfForAllEmployees();
}
