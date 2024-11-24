package org.harmoniapp.harmoniwebapi.services.importexport;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Service for importing users from an Excel file.
 */
@Service
@RequiredArgsConstructor
public class ImportService {
    private final UserExcelImport userExcelImport;
    private final ScheduleExcelImport scheduleExcelImport;

    /**
     * Imports users from an Excel file.
     *
     * @param file the Excel file to import.
     * @return a ResponseEntity with the result of the import operation.
     */
    public ResponseEntity<String> importUsersFromExcel(MultipartFile file) {
        Sheet sheet = readWorkbook(file);
        return userExcelImport.importUsers(sheet);
    }

    /**
     * Imports schedule from an Excel file.
     *
     * @param file the Excel file to import.
     * @return a ResponseEntity with the result of the import operation.
     */
    public ResponseEntity<String> importScheduleFromExcel(MultipartFile file) {
        Sheet sheet = readWorkbook(file);
        return scheduleExcelImport.importScheduleFromExcel(sheet);
    }

    /**
     * Reads the first sheet from the provided Excel file.
     *
     * @param file the Excel file to read.
     * @return the first sheet in the workbook.
     * @throws IllegalArgumentException if the file is not found, cannot be read, or the sheet is not found.
     */
    private Sheet readWorkbook(MultipartFile file) {
        Sheet sheet;
        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            sheet = wb.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found");
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found");
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading file");
        }
        return sheet;
    }
}
