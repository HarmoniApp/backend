package org.harmoniapp.services.importexport;

import org.springframework.core.io.InputStreamResource;

/**
 * Service interface for exporting data to Excel files.
 */
public interface ExcelExportService {

    /**
     * Exports user data to an Excel file.
     *
     * @return an InputStreamResource containing the Excel file
     */
    InputStreamResource exportUsers();

    /**
     * Exports shift data to an Excel file within the specified date range.
     *
     * @param start the start date of the range
     * @param end   the end date of the range
     * @return an InputStreamResource containing the Excel file
     */
    InputStreamResource exportShifts(String start, String end);
}
