package org.harmoniapp.services.importexport;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.harmoniapp.contracts.user.UserDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for exporting user data to an Excel file.
 * Extends the ExcelExport class and implements the ExportUser interface.
 */
@Service
@RequiredArgsConstructor
public class UserExcelExport extends ExcelExport implements ExportUser {
    private final UserDataService userDataService;
    private final List<String> headersCell = List.of("ID Pracownika", "Imie", "Nazwisko", "Mail", "Numer telefonu", "Miasto", "Ulica",
            "Numer mieszkania", "Kod pocztowy", "Numer budynku", "Role", "Jezyki",
            "Typ umowy", "Podpisanie umowy", "Wygasniecie umowy",
            "ID Przelozonego", "Oddzial");


    /**
     * Exports the list of active users to an Excel file.
     *
     * @return an InputStreamResource containing the exported data
     */
    @Override
    public InputStreamResource exportUsers() {
        List<UserDto> users = userDataService.getAllUsers();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pracownicy");
        createHeaderRow(sheet);
        for (int i = 0; i < users.size(); i++) {
            createEmployRow(sheet, users.get((i)), i);
        }
        for (int i = 0; i < headersCell.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        return writeFile(workbook);
    }

    /**
     * Creates the header row in the given sheet.
     *
     * @param sheet the sheet where the header row will be created
     */
    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headersCell.size(); i++) {
            headerRow.createCell(i).setCellValue(headersCell.get(i));
        }
    }

    /**
     * Creates a row in the given sheet for the specified user.
     *
     * @param sheet  the sheet where the row will be created
     * @param user   the user data to be filled in the row
     * @param rowIdx the index of the row to be created
     */
    private void createEmployRow(Sheet sheet, UserDto user, int rowIdx) {
        Row row = sheet.createRow(rowIdx + 1);
        for (int i = 0; i < headersCell.size(); i++) {
            fillCell(row, user, i);
        }
    }

    /**
     * Fills a cell in the given row with the appropriate user data based on the cell index.
     *
     * @param row     the row where the cell will be created
     * @param user    the user data to be filled in the cell
     * @param cellIdx the index of the cell to be created
     */
    private void fillCell(Row row, UserDto user, int cellIdx) {
        switch (cellIdx) {
            case 0 -> row.createCell(cellIdx).setCellValue(user.employeeId());
            case 1 -> row.createCell(cellIdx).setCellValue(user.firstname());
            case 2 -> row.createCell(cellIdx).setCellValue(user.surname());
            case 3 -> row.createCell(cellIdx).setCellValue(user.email());
            case 4 -> row.createCell(cellIdx).setCellValue(user.phoneNumber());
            case 5 -> row.createCell(cellIdx).setCellValue(user.residence().city());
            case 6 -> row.createCell(cellIdx).setCellValue(user.residence().street());
            case 7 -> row.createCell(cellIdx).setCellValue(user.residence().apartment());
            case 8 -> row.createCell(cellIdx).setCellValue(user.residence().zipCode());
            case 9 -> row.createCell(cellIdx).setCellValue(user.residence().buildingNumber());
            case 10 -> row.createCell(cellIdx).setCellValue(userDataService.getRoles(user));
            case 11 -> row.createCell(cellIdx).setCellValue(userDataService.getLanguages(user));
            case 12 -> row.createCell(cellIdx).setCellValue(user.contractType().name());
            case 13 -> row.createCell(cellIdx).setCellValue(user.contractSignature().toString());
            case 14 -> row.createCell(cellIdx).setCellValue(user.contractExpiration().toString());
            case 15 -> row.createCell(cellIdx).setCellValue(userDataService.getSupervisorEmployeeId(user));
            case 16 -> row.createCell(cellIdx).setCellValue(user.workAddress().departmentName());
        }
    }
}
