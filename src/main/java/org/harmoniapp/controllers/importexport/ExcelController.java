package org.harmoniapp.controllers.importexport;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.services.importexport.ExportExcelService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelController {
    private final ExportExcelService exportExcelService;

    @GetMapping("users/export-excel")
    public ResponseEntity<InputStreamResource> exportUsersToExcel() {
        return exportExcelService.exportUsers();
    }

    @GetMapping("shifts/export-excel")
    public ResponseEntity<InputStreamResource> exportShiftsToExcel(@RequestParam("start") String start, @RequestParam("end") String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return exportExcelService.exportShifts(startDate, endDate);
    }
}
