package org.harmoniapp.services.importexport;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.springframework.core.io.InputStreamResource;

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
     * Converts a ByteArrayOutputStream to an InputStreamResource.
     *
     * @param outputStream the ByteArrayOutputStream containing the PDF data
     * @return an InputStreamResource containing the PDF data
     */
    public InputStreamResource convertToInputStreamResource(ByteArrayOutputStream outputStream) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return new InputStreamResource(inputStream);
    }
}
