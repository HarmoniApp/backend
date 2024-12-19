package org.harmoniapp.services.importexport;

import org.apache.poi.ss.usermodel.Workbook;
import org.harmoniapp.exception.FileGenerationException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Abstract class for exporting data to Excel format.
 */
public abstract class ExcelExport {

    /**
     * Writes the given workbook to a ByteArrayInputStream.
     *
     * @param workbook the workbook to be written
     * @return a ByteArrayInputStream containing the workbook data
     * @throws FileGenerationException if an I/O error occurs during writing
     */
    protected ByteArrayInputStream writeFile(Workbook workbook) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
        } catch (IOException e) {
            throw new FileGenerationException(e.getMessage());
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * Creates a ResponseEntity containing the Excel file as an InputStreamResource.
     *
     * @param in the ByteArrayInputStream containing the Excel file data
     * @return ResponseEntity containing the Excel file as an InputStreamResource
     */
    protected ResponseEntity<InputStreamResource> createResponse(ByteArrayInputStream in) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=pracownicy.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }
}
