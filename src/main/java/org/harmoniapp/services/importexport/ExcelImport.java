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

/**
 * Abstract class for importing data from Excel files.
 */
public abstract class ExcelImport {

    /**
     * Reads the first sheet from the provided Excel file.
     *
     * @param file the Excel file to read.
     * @return the first sheet in the workbook.
     * @throws IllegalArgumentException     if the file is not found, cannot be read.
     * @throws EmptyFileException           if the file is empty.
     * @throws UnsupportedFileTypeException if the file is not an Excel file.
     */
    protected Sheet readSheet(@NotNull MultipartFile file) {
        Sheet sheet;
        assert file.getOriginalFilename() != null;
        if (!(file.getOriginalFilename().toLowerCase().endsWith(".xlsx") || file.getOriginalFilename().toLowerCase().endsWith(".xls"))) {
            throw new UnsupportedFileTypeException("Plik musi być w formacie Excel");
        }
        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            sheet = wb.getSheetAt(0);
            if (sheet == null) {
                throw new EmptyFileException("Nie znaleziono arkusza w pliku");
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Plk nie został znaleziony");
        } catch (IOException e) {
            throw new IllegalArgumentException("Błąd odczytu pliku");
        }
        return sheet;
    }
}
