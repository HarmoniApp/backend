package org.harmoniapp.services.importexport;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.harmoniapp.contracts.profile.AddressDto;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.entities.profile.Address;
import org.harmoniapp.entities.profile.ContractType;
import org.harmoniapp.entities.profile.Language;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EmptyFileException;
import org.harmoniapp.exception.FileGenerationException;
import org.harmoniapp.exception.InvalidCellException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.services.user.UserServiceImpl;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Service class for importing users from an Excel file.
 * Extends the ExcelImport class and implements the ImportUser interface.
 */
@Service
@RequiredArgsConstructor
public class UserExcelImport extends ExcelImport implements ImportUser {
    private final RepositoryCollector repositoryCollector;
    private final UserServiceImpl userService;
    private final List<String> expectedHeaders = List.of("employee id", "first name", "surname", "email",
            "phone number", "city", "street", "apartment", "zip code", "building number", "roles", "languages",
            "contract type", "contract signature", "contract expiration", "supervisor employee id", "department name");

    /**
     * Imports users from an Excel file.
     *
     * @param file the Excel file containing user data.
     * @return an InputStreamResource containing the result of the import operation.
     * @throws EmptyFileException   if the file is empty.
     * @throws InvalidCellException if the headers are invalid or an error occurs during import.
     */
    @Transactional
    public InputStreamResource importUsers(MultipartFile file) {
        Sheet sheet = readSheet(file);
        Iterator<Row> rows = sheet.rowIterator();
        if (!rows.hasNext()) {
            throw new EmptyFileException("Empty file");
        }
        List<String> headers = extractHeaders(rows.next());
        if (headers == null) {
            throw new InvalidCellException("Invalid headers");
        }

        List<UserDto> userDtoList;
        userDtoList = createUserDtoListFromSpreadsheet(rows, headers);

        List<UserDto> savedUsers = saveUsers(userDtoList);
        updateSupervisors(sheet, headers, savedUsers);

        return generateResponse(savedUsers);
    }

    /**
     * Extracts headers from the given header row.
     *
     * @param headerRow the row containing the headers.
     * @return a list of header names, or null if the headers are invalid.
     */
    private List<String> extractHeaders(Row headerRow) {
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(cell.getStringCellValue().toLowerCase());
        }
        if (new HashSet<>(headers).size() != headers.size() || !new HashSet<>(headers).containsAll(expectedHeaders)) {
            return null;
        }
        return headers;
    }

    /**
     * Saves a list of user DTOs to the database.
     *
     * @param userDtoList the list of user DTOs to save.
     * @return a list of saved user DTOs.
     * @throws InvalidCellException if a user cannot be saved.
     */
    private List<UserDto> saveUsers(List<UserDto> userDtoList) {
        List<UserDto> savedUsers = new ArrayList<>();
        for (UserDto userDto : userDtoList) {
            try {
                //TODO: improve adding users to the database
                UserDto newUser = userService.create(userDto);
                savedUsers.add(newUser);
            } catch (Exception e) {
                throw new InvalidCellException("Invalid row: " + (userDtoList.indexOf(userDto) + 2));
            }
        }
        return savedUsers;
    }

    /**
     * Updates the supervisors for the saved users.
     *
     * @param sheet      the Excel sheet containing user data.
     * @param headers    the list of headers from the Excel sheet.
     * @param savedUsers the list of saved user DTOs.
     */
    private void updateSupervisors(Sheet sheet, List<String> headers, List<UserDto> savedUsers) {
        List<User> supervisors = repositoryCollector.getUsers().findAllActiveSupervisors();
        List<User> usersToUpdate = new ArrayList<>();
        for (int i = 0; i < savedUsers.size(); i++) {
            UserDto userDto = savedUsers.get(i);
            if (userDto == null || userDto.supervisorId() != null) {
                continue;
            }
            User user = repositoryCollector.getUsers().findById(userDto.id()).orElseThrow();
            var sup = sheet.getRow(i + 1).getCell(headers.indexOf("supervisor employee id"));
            user.setSupervisor(supervisors.stream()
                    .filter(s -> s.getEmployeeId().equals(sup.getStringCellValue()))
                    .findFirst().orElse(null));
            usersToUpdate.add(user);
        }
        repositoryCollector.getUsers().saveAll(usersToUpdate);
    }

    /**
     * Generates a response indicating the result of the import operation.
     *
     * @param savedUsers the list of saved user DTOs.
     * @return an InputStreamResource containing the response data.
     */
    private InputStreamResource generateResponse(List<UserDto> savedUsers) {
        List<UserDto> response = createResponseList(savedUsers);

        byte[] pdfData = generatePdf(response);
        ByteArrayInputStream bis = new ByteArrayInputStream(pdfData);

        return new InputStreamResource(bis);
    }

    /**
     * Creates a list of UserDto objects containing only the employee ID, email, and password
     * from the provided list of saved UserDto objects.
     *
     * @param savedUsers the list of saved UserDto objects
     * @return a list of UserDto objects with limited fields
     */
    private List<UserDto> createResponseList(List<UserDto> savedUsers) {
        List<UserDto> response = new ArrayList<>();
        for (UserDto savedUser : savedUsers) {
            UserDto dto = UserDto.builder()
                    .employeeId(savedUser.employeeId())
                    .email(savedUser.email())
                    .password(savedUser.password())
                    .build();
            response.add(dto);
        }
        return response;
    }

    /**
     * Generates a PDF document from a list of UserDto objects.
     *
     * @param response the list of UserDto objects to include in the PDF
     * @return a byte array representing the generated PDF
     * @throws FileGenerationException if an error occurs while generating the PDF
     */
    private byte[] generatePdf(List<UserDto> response) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Color.BLACK);
            PdfPTable table = createPdfTable(response, font);
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new FileGenerationException("Error while generating PDF");
        }

        return out.toByteArray();
    }

    /**
     * Creates a PDF table with user information.
     *
     * @param response the list of UserDto containing user information
     * @param font     the font to be used in the table
     * @return a PdfPTable containing the user information
     * @throws DocumentException if there is an error creating the table
     */
    private PdfPTable createPdfTable(List<UserDto> response, Font font) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(95);
        table.setWidths(new int[]{2, 1, 4, 1, 4});

        for (UserDto user : response) {
            addTableCell(table, user.employeeId(), font);
            addTableCell(table, "login: ", font);
            addTableCell(table, user.email(), font);
            addTableCell(table, "haslo: ", font);
            addTableCell(table, user.password(), font);
        }

        return table;
    }

    /**
     * Adds a cell to the given PDF table with the specified text and font.
     *
     * @param table the PDF table to which the cell is added
     * @param text  the text to be displayed in the cell
     * @param font  the font to be used for the cell text
     */
    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(10);
        cell.setNoWrap(true);
        table.addCell(cell);
    }

    /**
     * Creates a list of user DTOs from the spreadsheet.
     *
     * @param rows    the iterator over the rows of the spreadsheet.
     * @param headers the list of headers from the spreadsheet.
     * @return a list of user DTOs created from the spreadsheet.
     * @throws RuntimeException if a row is invalid.
     */
    private List<UserDto> createUserDtoListFromSpreadsheet(Iterator<Row> rows, List<String> headers) throws RuntimeException {
        List<Role> roles = repositoryCollector.getRoles().findAll();
        List<Language> languages = repositoryCollector.getLanguages().findAll();
        List<ContractType> contractTypes = repositoryCollector.getContractTypes().findAll();
        List<User> supervisors = repositoryCollector.getUsers().findAllActiveSupervisors();
        List<Address> departments = repositoryCollector.getAddresses().findByDepartmentNameNotNull();
        List<UserDto> userDtoList = new ArrayList<>();

        while (rows.hasNext()) {
            Row row = rows.next();
            validateRow(row);
            UserDto userDto = createUserDtoFromRow(row, headers, roles, languages, contractTypes, supervisors, departments);
            userDtoList.add(userDto);
        }

        return userDtoList;
    }

    /**
     * Validates the given row.
     *
     * @param row the row to validate.
     * @throws InvalidCellException if the row is invalid.
     */
    private void validateRow(Row row) {
        if (row.getLastCellNum() != expectedHeaders.size()) {
            throw new InvalidCellException("Invalid row: " + row.getRowNum());
        }
    }

    /**
     * Creates a UserDto from a row in the spreadsheet.
     *
     * @param row           the row containing user data.
     * @param headers       the list of headers from the spreadsheet.
     * @param roles         the list of roles from the database.
     * @param languages     the list of languages from the database.
     * @param contractTypes the list of contract types from the database.
     * @param supervisors   the list of supervisors from the database.
     * @param departments   the list of departments from the database.
     * @return a UserDto created from the row data.
     * @throws InvalidCellException if the row contains invalid data.
     */
    private UserDto createUserDtoFromRow(Row row, List<String> headers, List<Role> roles, List<Language> languages,
                                         List<ContractType> contractTypes, List<User> supervisors, List<Address> departments) {
        Iterator<Cell> cells = row.cellIterator();
        var userBuilder = UserDto.builder();
        var addressBuilder = AddressDto.builder();

        while (cells.hasNext()) {
            Cell cell = cells.next();
            populateUserAndAddressBuilders(cell, headers.get(cell.getColumnIndex()), userBuilder, addressBuilder, roles, languages, contractTypes, supervisors, departments);
        }

        UserDto preview = userBuilder.build();
        if (preview.languages().isEmpty() || preview.roles().isEmpty()) {
            throw new InvalidCellException("Invalid row: " + row.getRowNum());
        }

        return userBuilder.residence(addressBuilder.build()).build();
    }

    /**
     * Populates the user and address builders with data from the given cell.
     *
     * @param cell           the cell containing the data.
     * @param header         the header corresponding to the cell.
     * @param userBuilder    the builder for creating a UserDto.
     * @param addressBuilder the builder for creating an AddressDto.
     * @param roles          the list of roles from the database.
     * @param languages      the list of languages from the database.
     * @param contractTypes  the list of contract types from the database.
     * @param supervisors    the list of supervisors from the database.
     * @param departments    the list of departments from the database.
     */
    private void populateUserAndAddressBuilders(Cell cell, String header, UserDto.UserDtoBuilder userBuilder,
                                                AddressDto.AddressDtoBuilder addressBuilder, List<Role> roles, List<Language> languages,
                                                List<ContractType> contractTypes, List<User> supervisors, List<Address> departments) {
        cell.setCellType(CellType.STRING);
        switch (header) {
            case "employee id" -> userBuilder.employeeId(cell.getStringCellValue());
            case "first name" -> userBuilder.firstname(cell.getStringCellValue());
            case "surname" -> userBuilder.surname(cell.getStringCellValue());
            case "email" -> userBuilder.email(cell.getStringCellValue());
            case "phone number" -> userBuilder.phoneNumber(cell.getStringCellValue());
            case "city" -> addressBuilder.city(cell.getStringCellValue());
            case "street" -> addressBuilder.street(cell.getStringCellValue());
            case "apartment" -> addressBuilder.apartment(cell.getStringCellValue());
            case "zip code" -> addressBuilder.zipCode(cell.getStringCellValue());
            case "building number" -> addressBuilder.buildingNumber(cell.getStringCellValue());
            case "roles" -> userBuilder.roles(getRoles(cell.getStringCellValue(), roles));
            case "languages" -> userBuilder.languages(getLanguages(cell.getStringCellValue(), languages));
            case "contract type" -> userBuilder.contractType(getContractType(cell.getStringCellValue(), contractTypes));
            case "contract signature" -> userBuilder.contractSignature(LocalDate.parse(cell.getStringCellValue()));
            case "contract expiration" -> userBuilder.contractExpiration(LocalDate.parse(cell.getStringCellValue()));
            case "supervisor employee id" ->
                    userBuilder.supervisorId(getSupervisorId(cell.getStringCellValue(), supervisors));
            case "department name" -> userBuilder.workAddress(getDepartment(cell.getStringCellValue(), departments));
        }
    }

    /**
     * Retrieves a list of roles based on the provided role names.
     *
     * @param roles    the comma-separated role names.
     * @param roleList the list of roles to filter from.
     * @return a list of roles that match the provided names.
     */
    private List<RoleDto> getRoles(String roles, List<Role> roleList) {
        return roleList.stream()
                .filter(r -> List.of(roles.split(",")).contains(r.getName()))
                .map(RoleDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves a list of language DTOs based on the provided language names.
     *
     * @param languages    the comma-separated language names.
     * @param languageList the list of languages to filter from.
     * @return a list of language DTOs that match the provided names.
     */
    private List<LanguageDto> getLanguages(String languages, List<Language> languageList) {
        return languageList.stream()
                .filter(l -> List.of(languages.split(",")).contains(l.getName()))
                .map(LanguageDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves a contract type based on the provided contract type name.
     *
     * @param contractTypeName the name of the contract type to find.
     * @param contractTypes    the list of contract types to search.
     * @return the matching contract type, or null if no match is found.
     */
    private ContractType getContractType(String contractTypeName, List<ContractType> contractTypes) {
        return contractTypes.stream()
                .filter(c -> c.getName().equals(contractTypeName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves the supervisor ID based on the provided supervisor employee ID.
     *
     * @param supervisorEmployeeId the employee ID of the supervisor to find.
     * @param supervisors          the list of supervisors to search.
     * @return the ID of the matching supervisor, or -1L if no match is found.
     */
    private Long getSupervisorId(String supervisorEmployeeId, List<User> supervisors) {
        return supervisors.stream()
                .filter(u -> u.getEmployeeId().equals(supervisorEmployeeId))
                .findFirst()
                .map(User::getId)
                .orElse(-1L);
    }

    /**
     * Retrieves the department address based on the provided department name.
     *
     * @param departmentName the name of the department to find.
     * @param departments    the list of departments to search.
     * @return the address of the matching department, or null if no match is found.
     */
    private AddressDto getDepartment(String departmentName, List<Address> departments) {
        return departments.stream()
                .filter(a -> a.getDepartmentName().equals(departmentName))
                .map(AddressDto::fromEntity)
                .findFirst()
                .orElse(null);
    }
}
