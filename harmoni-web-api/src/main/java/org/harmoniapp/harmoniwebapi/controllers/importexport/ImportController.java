package org.harmoniapp.harmoniwebapi.controllers.importexport;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.services.importexport.ImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ImportController {
    private final ImportService service;

    @PostMapping("/users/import-excel")
    public ResponseEntity<String> importUsersFromExcel(@RequestPart MultipartFile file) {
        return service.importUsersFromExcel(file);
    }
}
