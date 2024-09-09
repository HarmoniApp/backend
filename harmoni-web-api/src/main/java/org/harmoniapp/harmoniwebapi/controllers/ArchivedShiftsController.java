package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.ArchivedShiftDto;
import org.harmoniapp.harmoniwebapi.services.ArchivedShiftsService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing archived shifts.
 * Provides endpoints to generate PDFs for shifts and retrieve archived shifts data.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/archived-shifts")
@CrossOrigin(origins = "http://localhost:3000")
public class ArchivedShiftsController {
    private final ArchivedShiftsService archivedShiftsService;

    /**
     * Generates a PDF report for the shifts that occurred in the specified week.
     *
     * @param startOfWeek the start date of the week for which the report will be generated
     * @return ResponseEntity containing the generated PDF as InputStreamResource
     */
    @GetMapping("/generate-pdf")
    public ResponseEntity<InputStreamResource> generatePdfForWeek(@RequestParam("startOfWeek") LocalDate startOfWeek) {
        return archivedShiftsService.generatePdfForWeek(startOfWeek);
    }

    /**
     * Retrieves a list of all archived shifts.
     *
     * @return a list of ArchivedShiftDto containing
     */
    @GetMapping
    public List<ArchivedShiftDto> getAllArchivedShifts() {
        return archivedShiftsService.getAllArchivedShifts();
    }

    /**
     * Retrieves a specific archived shift by its ID and returns the PDF file.
     *
     * @param id the ID of the archived shift to retrieve
     * @return ResponseEntity containing the archived shift PDF as InputStreamResource
     */
    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> getArchivedShift(@PathVariable long id) {
        return archivedShiftsService.getArchivedShift(id);
    }
}
