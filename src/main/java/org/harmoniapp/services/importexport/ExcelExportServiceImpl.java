package org.harmoniapp.services.importexport;


import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service implementation for exporting data to Excel files.
 */
@Service
@RequiredArgsConstructor
public class ExcelExportServiceImpl implements ExcelExportService {
    private final ExportUser userExcelExport;
    private final ExportSchedule scheduleExcelExport;

    /**
     * Exports user data to an Excel file.
     *
     * @return an InputStreamResource containing the Excel file
     */
    @Override
    public InputStreamResource exportUsers() {
        return userExcelExport.exportUsers();
    }

    /**
     * Exports shift data to an Excel file for the given date range.
     *
     * @param start the start date of the date range.
     * @param end   the end date of the date range.
     * @return an InputStreamResource containing the Excel file.
     */
    @Override
    public InputStreamResource exportShifts(String start, String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return scheduleExcelExport.exportShifts(startDate, endDate);
    }
}
