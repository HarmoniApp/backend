package org.harmoniapp.services.importexport;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for importing users from an Excel file.
 */
@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {
    private final ImportUser userExcelImport;
    private final ImportSchedule scheduleExcelImport;

    /**
     * Imports users from an Excel file.
     *
     * @param file the Excel file to import.
     * @return an InputStreamResource containing the imported users.
     */
    public InputStreamResource importUsersFromExcel(MultipartFile file) {
        return userExcelImport.importUsers(file);
    }

    /**
     * Imports schedule from an Excel file.
     *
     * @param file the Excel file to import.
     * @return a status message.
     */
    public String importScheduleFromExcel(MultipartFile file) {
        return scheduleExcelImport.importSchedule(file);
    }
}
