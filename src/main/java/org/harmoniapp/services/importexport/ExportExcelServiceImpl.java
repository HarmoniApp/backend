package org.harmoniapp.services.importexport;


import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service implementation for exporting data to Excel files.
 */
@Service
@RequiredArgsConstructor
public class ExportExcelServiceImpl implements ExportExcelService {
    private final ExportUser userExcelExport;
    private final ExportSchedule scheduleExcelExport;

    /**
     * Exports user data to an Excel file.
     *
     * @return ResponseEntity containing the InputStreamResource of the exported Excel file.
     */
    @Override
    public ResponseEntity<InputStreamResource> exportUsers() {
        return userExcelExport.exportUsers();
    }

    /**
     * Exports shift data to an Excel file for the given date range.
     *
     * @param startDate the start date of the date range.
     * @param endDate the end date of the date range.
     * @return ResponseEntity containing the InputStreamResource of the exported Excel file.
     */
    @Override
    public ResponseEntity<InputStreamResource> exportShifts(LocalDate startDate, LocalDate endDate) {
        return scheduleExcelExport.exportShifts(startDate, endDate);
    }
}
