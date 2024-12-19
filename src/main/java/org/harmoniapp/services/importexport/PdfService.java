package org.harmoniapp.services.importexport;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class PdfService {
    private final ExportUser userPdfExport;
    private final ExportSchedule schedulePdfExport;

    public ResponseEntity<InputStreamResource> generatePdfForWeek(LocalDate startOfWeek) {
        LocalDate end = startOfWeek.plusDays(6);
        return schedulePdfExport.exportShifts(startOfWeek, end);
    }

    public ResponseEntity<InputStreamResource> generatePdfForAllEmployees() {
        return userPdfExport.exportUsers();
    }
}
