package org.harmoniapp.controllers.importexport;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.services.importexport.PdfService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for managing archived shifts.
 * Provides endpoints to generate PDFs for shifts and retrieve archived shifts data.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pdf")
public class PdfController {
    private final PdfService pdfService;

    /**
     * Generates a PDF report for the shifts that occurred in the specified week.
     *
     * @param startOfWeek the start date of the week for which the report will be generated
     * @return ResponseEntity containing the generated PDF as InputStreamResource
     */
    @GetMapping("/generate-pdf-shift")
    public ResponseEntity<InputStreamResource> generatePdfForWeek(@RequestParam("startOfWeek") LocalDate startOfWeek) {
        return pdfService.generatePdfForWeek(startOfWeek);
    }

    @GetMapping("/generate-pdf-all-employees")
    public ResponseEntity<InputStreamResource> generatePdfForAllEmployees() {
        return pdfService.generatePdfForAllEmployees();
    }
}
