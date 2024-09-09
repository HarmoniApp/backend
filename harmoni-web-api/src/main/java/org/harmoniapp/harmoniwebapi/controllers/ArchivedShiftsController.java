package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.ArchivedShiftDto;
import org.harmoniapp.harmoniwebapi.services.ArchivedShiftsService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/archived-shifts")
@CrossOrigin(origins = "http://localhost:3000")
public class ArchivedShiftsController {
    private final ArchivedShiftsService archivedShiftsService;

    @GetMapping("/generate-pdf")
    public ResponseEntity<InputStreamResource> generatePdfForWeek(@RequestParam("startOfWeek") LocalDate startOfWeek) {
        return archivedShiftsService.generatePdfForWeek(startOfWeek);
    }

    @GetMapping
    public List<ArchivedShiftDto> getAllArchivedShifts() {
        return archivedShiftsService.getAllArchivedShifts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> getArchivedShift(@PathVariable long id) {
        return archivedShiftsService.getArchivedShift(id);
    }
}
