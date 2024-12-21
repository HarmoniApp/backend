package org.harmoniapp.services.importexport;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for importing schedules.
 */
public interface ImportSchedule {

    /**
     * Imports a schedule from a given file.
     *
     * @param file the file containing the schedule to import
     * @return a status message
     */
    String importSchedule(MultipartFile file);
}
