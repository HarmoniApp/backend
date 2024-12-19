package org.harmoniapp.services.importexport;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Abstract class for exporting data to PDF format.
 */
public abstract class PdfExport {

    /**
     * Adds a header row to the PDF table.
     *
     * @param headersCell a list of header titles
     * @param table       the table to add the header to
     */
    protected void addTableHeader(List<String> headersCell, PdfPTable table) {
        headersCell.forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA);
            header.setBackgroundColor(Color.ORANGE);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle, headFont));
            table.addCell(header);
        });
    }

    /**
     * Creates a ResponseEntity containing the PDF file.
     *
     * @param out the ByteArrayOutputStream containing the PDF data
     * @return a ResponseEntity containing the PDF file as an InputStreamResource
     */
    protected ResponseEntity<InputStreamResource> createResponseEntity(ByteArrayOutputStream out) {
        ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
