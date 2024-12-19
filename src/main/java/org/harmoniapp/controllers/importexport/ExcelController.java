package org.harmoniapp.controllers.importexport;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.services.importexport.ExcelExportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelController {
    private final ExcelExportService excelExportService;

    @GetMapping("users/export-excel")
    public ResponseEntity<InputStreamResource> exportUsersToExcel() {
        return excelExportService.exportUsers();
    }

    @GetMapping("shifts/export-excel")
    public ResponseEntity<InputStreamResource> exportShiftsToExcel(@RequestParam("start") String start, @RequestParam("end") String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return excelExportService.exportShifts(startDate, endDate);
    }
}
