package org.harmoniapp.controllers.importexport;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.services.importexport.ExcelExportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling Excel import/export operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelController {
    private final ExcelExportService excelExportService;
    private final MediaType mediaType = MediaType.parseMediaType("application/vnd.ms-excel");

    /**
     * Exports user data to an Excel file.
     *
     * @return a ResponseEntity containing the InputStreamResource of the Excel file
     */
    @GetMapping("users/export-excel")
    public ResponseEntity<InputStreamResource> exportUsersToExcel() {
        InputStreamResource resource = excelExportService.exportUsers();
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    /**
     * Exports shift data to an Excel file for the specified date range.
     *
     * @param start the start date of the range in ISO-8601 format (yyyy-MM-dd)
     * @param end   the end date of the range in ISO-8601 format (yyyy-MM-dd)
     * @return a ResponseEntity containing the InputStreamResource of the Excel file
     */
    @GetMapping("shifts/export-excel")
    public ResponseEntity<InputStreamResource> exportShiftsToExcel(@RequestParam("start") String start, @RequestParam("end") String end) {
        InputStreamResource resource = excelExportService.exportShifts(start, end);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}
