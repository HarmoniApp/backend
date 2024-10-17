package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.services.ExcelService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
@CrossOrigin(origins = "http://localhost:3000")
public class ExcelController {
    private final ExcelService excelService;

    @GetMapping("users/export-excel")
    public ResponseEntity<InputStreamResource> exportToExcel() {
        return excelService.exportUsersToExcel();
    }

}
