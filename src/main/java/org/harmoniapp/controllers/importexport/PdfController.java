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


//    /**
//     * Retrieves a list of all archived shifts.
//     *
//     * @return a list of ArchivedShiftDto containing
//     */
//    @GetMapping
//    public List<ArchivedShiftDto> getAllArchivedShifts() {
//        return archivedShiftService.getAllArchivedShifts();
//    }

//    /**
//     * Retrieves a specific archived shift by its ID and returns the PDF file.
//     *
//     * @param id the ID of the archived shift to retrieve
//     * @return ResponseEntity containing the archived shift PDF as InputStreamResource
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<InputStreamResource> getArchivedShift(@PathVariable long id) {
//        return archivedShiftService.getArchivedShift(id);
//    }
}
