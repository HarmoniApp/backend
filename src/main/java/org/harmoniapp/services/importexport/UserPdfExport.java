package org.harmoniapp.services.importexport;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.exception.FileGenerationException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Service for exporting user data to a PDF file.
 * Extends PdfExport and implements ExportUser.
 */
@Service
@RequiredArgsConstructor
public class UserPdfExport extends PdfExport implements ExportUser {
    private final UserDataService userDataService;
    private final List<String> headersCell = List.of("ID Pracownika", "Imie", "Nazwisko", "Mail",
            "Numer telefonu", "Miasto", "Ulica",
            "Numer mieszkania", "Kod pocztowy", "Numer budynku", "Role", "Jezyki",
            "Typ umowy", "Podpisanie umowy", "Wygasniecie umowy",
            "ID Przelozonego", "Oddzial");

    /**
     * Exports user data to a PDF file.
     *
     * @return an InputStreamResource containing the exported data
     */
    @Override
    public InputStreamResource exportUsers() {
        List<UserDto> users = userDataService.getAllUsers();
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeDocument(document, out, users);
        return convertToInputStreamResource(out);
    }

    /**
     * Writes the user data to the PDF document.
     *
     * @param document the document to write to
     * @param out      the output stream to write to
     * @param users    the list of users to include in the document
     */
    void writeDocument(Document document, ByteArrayOutputStream out, List<UserDto> users) {
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            addTitle(document);
            addTable(document, users);
            document.close();
        } catch (DocumentException e) {
            throw new FileGenerationException("Nie udało się wygenerować pliku PDF. Spróbuj ponownie.");
        }
    }

    /**
     * Adds a title to the PDF document.
     *
     * @param document the document to add the title to
     * @throws DocumentException if there is an error adding the title
     */
    void addTitle(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
        Paragraph title = new Paragraph("Pracownicy: ", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds a table with user data to the PDF document.
     *
     * @param document the document to add the table to
     * @param users    the list of users to include in the table
     * @throws DocumentException if there is an error adding the table
     */
    void addTable(Document document, List<UserDto> users) throws DocumentException {
        PdfPTable table = new PdfPTable(headersCell.size());
        table.setWidthPercentage(100);
        addTableHeader(headersCell, table);
        addRows(table, users);
        document.add(table);
    }

    /**
     * Adds rows to the table with user data.
     *
     * @param table the table to add rows to
     * @param users the list of users to include in the rows
     */
    void addRows(PdfPTable table, List<UserDto> users) {
        for (UserDto user : users) {
            table.addCell(new PdfPCell(new Phrase(user.employeeId())));
            table.addCell(new PdfPCell(new Phrase(user.firstname())));
            table.addCell(new PdfPCell(new Phrase(user.surname())));
            table.addCell(new PdfPCell(new Phrase(user.email())));
            table.addCell(new PdfPCell(new Phrase(user.phoneNumber())));
            table.addCell(new PdfPCell(new Phrase(user.residence().city())));
            table.addCell(new PdfPCell(new Phrase(user.residence().street())));
            table.addCell(new PdfPCell(new Phrase(user.residence().apartment())));
            table.addCell(new PdfPCell(new Phrase(user.residence().zipCode())));
            table.addCell(new PdfPCell(new Phrase(user.residence().buildingNumber())));
            table.addCell(new PdfPCell(new Phrase(userDataService.getRoles(user))));
            table.addCell(new PdfPCell(new Phrase(userDataService.getLanguages(user))));
            table.addCell(new PdfPCell(new Phrase(user.contractType().name())));
            table.addCell(new PdfPCell(new Phrase(user.contractSignature().toString())));
            table.addCell(new PdfPCell(new Phrase(user.contractExpiration().toString())));
            table.addCell(new PdfPCell(new Phrase(userDataService.getSupervisorEmployeeId(user))));
            table.addCell(new PdfPCell(new Phrase(user.workAddress().departmentName())));
        }
    }
}
