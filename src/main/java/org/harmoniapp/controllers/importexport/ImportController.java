package org.harmoniapp.controllers.importexport;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.services.importexport.ImportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for handling Excel import operations.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ImportController {
    private final ImportService service;

    /**
     * Endpoint to import users from an Excel file.
     *
     * @param file the Excel file to import.
     * @return a ResponseEntity containing an InputStreamResource with the result of the import operation.
     */
    @PostMapping("/users/import-excel")
    public ResponseEntity<InputStreamResource> importUsersFromExcel(@RequestPart MultipartFile file) {
        return service.importUsersFromExcel(file);
    }

    /**
     * Endpoint to import schedule from an Excel file.
     *
     * @param file the Excel file to import.
     * @return a ResponseEntity with the result of the import operation.
     */
    @PostMapping("/shifts/import-excel")
    public ResponseEntity<String> importScheduleFromExcel(@RequestPart MultipartFile file) {
        return service.importScheduleFromExcel(file);
    }
}
