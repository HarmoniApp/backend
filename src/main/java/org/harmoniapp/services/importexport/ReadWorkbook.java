package org.harmoniapp.services.importexport;

import jakarta.validation.constraints.NotNull;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.harmoniapp.exception.EmptyFileException;
import org.harmoniapp.exception.UnsupportedFileTypeException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ReadWorkbook {
    /**
     * Reads the first sheet from the provided Excel file.
     *
     * @param file the Excel file to read.
     * @return the first sheet in the workbook.
     * @throws IllegalArgumentException     if the file is not found, cannot be read.
     * @throws EmptyFileException           if the file is empty.
     * @throws UnsupportedFileTypeException if the file is not an Excel file.
     */
    default Sheet readSheet(@NotNull MultipartFile file) {
        Sheet sheet;
        assert file.getOriginalFilename() != null;
        if (!(file.getOriginalFilename().toLowerCase().endsWith(".xlsx") || file.getOriginalFilename().toLowerCase().endsWith(".xls"))) {
            throw new UnsupportedFileTypeException("File must be an Excel file");
        }
        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            sheet = wb.getSheetAt(0);
            if (sheet == null) {
                throw new EmptyFileException("Sheet not found");
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found");
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading file");
        }
        return sheet;
    }
}
