package org.harmoniapp.services.importexport;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for importing data from Excel files.
 */
public interface ImportService {

    /**
     * Imports users from an Excel file.
     *
     * @param file the Excel file containing user data
     * @return a ResponseEntity containing an InputStreamResource
     */
    ResponseEntity<InputStreamResource> importUsersFromExcel(MultipartFile file);

    /**
     * Imports schedule from an Excel file.
     *
     * @param file the Excel file containing schedule data
     * @return a ResponseEntity containing a status message
     */
    ResponseEntity<String> importScheduleFromExcel(MultipartFile file);
}
