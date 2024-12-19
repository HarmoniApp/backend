package org.harmoniapp.services.importexport;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for importing schedules.
 */
public interface ImportSchedule {

    /**
     * Imports a schedule from a given file.
     *
     * @param file the file containing the schedule to import
     * @return a ResponseEntity with a message indicating the result of the import
     */
    ResponseEntity<String> importSchedule(MultipartFile file);
}
