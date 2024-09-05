package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.services.ShiftArchivalService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/archival")
@CrossOrigin(origins = "http://localhost:3000")
public class ShiftArchivalController {
    private final ShiftArchivalService shiftArchivalService;

    @GetMapping("/generate-pdf")
    public ResponseEntity<InputStreamResource> generatePdfForWeek(@RequestParam("startOfWeek") LocalDate startOfWeek) {
        return shiftArchivalService.generatePdfForWeek(startOfWeek);
    }
}
