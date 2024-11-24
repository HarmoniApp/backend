package org.harmoniapp.harmoniwebapi.services.importexport;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ReadWorkbook {
    /**
     * Reads the first sheet from the provided Excel file.
     *
     * @param file the Excel file to read.
     * @return the first sheet in the workbook.
     * @throws IllegalArgumentException if the file is not found, cannot be read, or the sheet is not found.
     */
    default Sheet readSheet(MultipartFile file) {
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
