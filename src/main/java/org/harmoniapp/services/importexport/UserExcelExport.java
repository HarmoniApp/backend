package org.harmoniapp.services.importexport;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.FileGenerationException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for exporting user data to an Excel file.
 */
@Service
@RequiredArgsConstructor
public class UserExcelExport implements ExportUser {
    private final RepositoryCollector repositoryCollector;
    private final List<String> headersCell = List.of("ID Pracownika", "Imie", "Nazwisko", "Mail", "Numer telefonu", "Miasto", "Ulica",
            "Numer mieszkania", "Kod pocztowy", "Numer budynku", "Role", "Jezyki",
            "Typ umowy", "Podpisanie umowy", "Wygasniecie umowy",
            "ID Przelozonego", "Oddzial");


    /**
     * Exports the list of active users to an Excel file.
     *
     * @return ResponseEntity containing the Excel file as an InputStreamResource
     */
    @Override
    public ResponseEntity<InputStreamResource> exportUsers() {
        List<UserDto> users = getAllUsers();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pracownicy");
        createHeaderRow(sheet);
        for (int i = 0; i < users.size(); i++) {
            createEmployRow(sheet, users.get((i)), i);
        }
        for (int i = 0; i < headersCell.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayInputStream in = writeFile(workbook);
        return createResponse(in);
    }

    /**
     * Retrieves all active users from the repository, sorted by surname and firstname.
     *
     * @return a list of UserDto objects representing active users
     */
    private List<UserDto> getAllUsers() {
        return repositoryCollector.getUsers()
                .findByIsActiveTrue(Sort.by("surname", "firstname"))
                .stream()
                .map(UserDto::fromEntity)
                .toList();
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
     * @param sheet the sheet where the row will be created
     * @param user the user data to be filled in the row
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
     * @param row the row where the cell will be created
     * @param user the user data to be filled in the cell
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
            case 10 -> row.createCell(cellIdx).setCellValue(getRoles(user));
            case 11 -> row.createCell(cellIdx).setCellValue(getLanguages(user));
            case 12 -> row.createCell(cellIdx).setCellValue(user.contractType().getName());
            case 13 -> row.createCell(cellIdx).setCellValue(user.contractSignature().toString());
            case 14 -> row.createCell(cellIdx).setCellValue(user.contractExpiration().toString());
            case 15 -> row.createCell(cellIdx).setCellValue(getSupervisorEmployeeId(user));
            case 16 -> row.createCell(cellIdx).setCellValue(user.workAddress().departmentName());
        }
    }

    /**
     * Retrieves the roles of the given user as a comma-separated string.
     *
     * @param user the user whose roles are to be retrieved
     * @return a comma-separated string of role names
     */
    private String getRoles(UserDto user) {
        return user.roles().stream()
                .map(RoleDto::name)
                .collect(Collectors.joining(", "));
    }

    /**
     * Retrieves the languages spoken by the given user as a comma-separated string.
     *
     * @param user the user whose languages are to be retrieved
     * @return a comma-separated string of language names
     */
    private String getLanguages(UserDto user) {
        return user.languages().stream()
                .map(LanguageDto::name)
                .collect(Collectors.joining(", "));
    }

    /**
     * Retrieves the employee ID of the supervisor for the given user.
     *
     * @param user the user whose supervisor's employee ID is to be retrieved
     * @return the employee ID of the supervisor, or an empty string if the supervisor ID is null or not found
     */
    private String getSupervisorEmployeeId(UserDto user) {
        if (user.supervisorId() != null) {
            return repositoryCollector.getUsers()
                    .findById(user.supervisorId())
                    .map(User::getEmployeeId)
                    .orElse("");
        }
        return "";
    }

    /**
     * Writes the given workbook to a ByteArrayInputStream.
     *
     * @param workbook the workbook to be written
     * @return a ByteArrayInputStream containing the workbook data
     * @throws FileGenerationException if an I/O error occurs during writing
     */
    private ByteArrayInputStream writeFile(Workbook workbook) {
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
    private ResponseEntity<InputStreamResource> createResponse(ByteArrayInputStream in) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=pracownicy.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }
}
