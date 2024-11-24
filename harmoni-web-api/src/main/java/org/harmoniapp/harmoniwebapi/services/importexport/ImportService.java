package org.harmoniapp.harmoniwebapi.services.importexport;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service for importing users from an Excel file.
 */
@Service
@RequiredArgsConstructor
public class ImportService {
    private final ImportUser userExcelImport;
    private final ImportSchedule scheduleExcelImport;

    /**
     * Imports users from an Excel file.
     *
     * @param file the Excel file to import.
     * @return a ResponseEntity containing a list of UserDto objects with the result of the import operation.
     */
    public ResponseEntity<List<UserDto>> importUsersFromExcel(MultipartFile file) {
        return userExcelImport.importUsers(file);
    }

    /**
     * Imports schedule from an Excel file.
     *
     * @param file the Excel file to import.
     * @return a ResponseEntity with the result of the import operation.
     */
    public ResponseEntity<String> importScheduleFromExcel(MultipartFile file) {
        return scheduleExcelImport.importSchedule(file);
    }
}
