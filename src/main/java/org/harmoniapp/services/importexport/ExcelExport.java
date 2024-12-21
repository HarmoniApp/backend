package org.harmoniapp.services.importexport;

import org.apache.poi.ss.usermodel.Workbook;
import org.harmoniapp.exception.FileGenerationException;
import org.springframework.core.io.InputStreamResource;

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
     * @return an InputStreamResource containing the Excel file data
     * @throws FileGenerationException if an I/O error occurs during writing
     */
    protected InputStreamResource writeFile(Workbook workbook) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
        } catch (IOException e) {
            throw new FileGenerationException(e.getMessage());
        }
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return new InputStreamResource(in);
    }
}
