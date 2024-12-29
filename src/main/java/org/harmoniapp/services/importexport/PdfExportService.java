package org.harmoniapp.services.importexport;

import org.springframework.core.io.InputStreamResource;

import java.time.LocalDate;

/**
 * Service interface for generating PDF reports.
 */
public interface PdfExportService {

    /**
     * Generates a PDF report for a specific week.
     *
     * @param startOfWeek the start date of the week
     * @return an InputStreamResource containing the generated PDF
     */
    InputStreamResource generatePdfForWeek(LocalDate startOfWeek);

    /**
     * Generates a PDF report for all employees.
     *
     * @return an InputStreamResource containing the generated PDF
     */
    InputStreamResource generatePdfForAllEmployees();
}
