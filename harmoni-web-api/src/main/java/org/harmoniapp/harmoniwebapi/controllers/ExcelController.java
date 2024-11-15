package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.services.ExcelService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelController {
    private final ExcelService excelService;

    @GetMapping("users/export-excel")
    public ResponseEntity<InputStreamResource> exportUsersToExcel() {
        return excelService.exportUsersToExcel();
    }

    @GetMapping("shifts/export-excel")
    public ResponseEntity<InputStreamResource> exportShiftsToExcel(@RequestParam("start") String start, @RequestParam("end") String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return excelService.exportShiftsToExcel(startDate, endDate);
    }


}
