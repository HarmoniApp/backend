package org.harmoniapp.services.importexport;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

/**
 * Service interface for exporting data to Excel files.
 */
public interface ExportExcelService {

    /**
     * Exports user data to an Excel file.
     *
     * @return a ResponseEntity containing the InputStreamResource of the Excel file.
     */
    ResponseEntity<InputStreamResource> exportUsers();

    /**
     * Exports shift data to an Excel file within the specified date range.
     *
     * @param startDate the start date of the date range.
     * @param endDate the end date of the date range.
     * @return a ResponseEntity containing the InputStreamResource of the Excel file.
     */
    ResponseEntity<InputStreamResource> exportShifts(LocalDate startDate, LocalDate endDate);
}
