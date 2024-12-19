package org.harmoniapp.services.importexport;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface ExportSchedule {
    ResponseEntity<InputStreamResource> exportShifts(LocalDate startDate, LocalDate endDate);
}
