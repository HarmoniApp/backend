package org.harmoniapp.services.importexport;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for importing data from Excel files.
 */
public interface ImportService {

    /**
     * Imports users from an Excel file.
     *
     * @param file the Excel file containing user data
     * @return an InputStreamResource containing the imported users
     */
    InputStreamResource importUsersFromExcel(MultipartFile file);

    /**
     * Imports schedule from an Excel file.
     *
     * @param file the Excel file containing schedule data
     * @return a status message
     */
    String importScheduleFromExcel(MultipartFile file);
}
