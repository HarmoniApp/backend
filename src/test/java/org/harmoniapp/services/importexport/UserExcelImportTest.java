package org.harmoniapp.services.importexport;

import org.apache.poi.ss.usermodel.*;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.entities.profile.Address;
import org.harmoniapp.entities.profile.ContractType;
import org.harmoniapp.entities.profile.Language;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.profile.AddressRepository;
import org.harmoniapp.repositories.profile.ContractTypeRepository;
import org.harmoniapp.repositories.profile.LanguageRepository;
import org.harmoniapp.repositories.profile.RoleRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.harmoniapp.services.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserExcelImportTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContractTypeRepository contractTypeRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserExcelImport userExcelImport;

    @Test
    public void importUsersTest() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        Workbook workbook = mock(Workbook.class);
        Sheet sheet = mock(Sheet.class);
        Row headerRow = mock(Row.class);

        UserDto userDto = mock(UserDto.class);
        Iterator<Row> rowIterator = mock(Iterator.class);
        List<UserDto> userDtoList = List.of(userDto);
        List<User> supervisors = List.of(mock(User.class));

        when(workbook.getSheetAt(0)).thenReturn(sheet);
        when(sheet.rowIterator()).thenReturn(rowIterator);
        when(rowIterator.hasNext()).thenReturn(true, true, false);
        when(rowIterator.next()).thenReturn(headerRow);
        when(file.getInputStream()).thenReturn(inputStream);
        when(file.getOriginalFilename()).thenReturn("file.xlsx");

        InputStreamResource inputStreamResource = mock(InputStreamResource.class);
        UserExcelImport userExcelImportSpy = spy(userExcelImport);
        List<String> headers = List.of("id pracownika", "imie", "nazwisko", "mail",
                "numer telefonu", "miasto", "ulica", "numer mieszkania", "kod pocztowy", "numer budynku", "role", "jezyki",
                "typ umowy", "podpisanie umowy", "wygasniecie umowy", "id przelozonego", "oddzial");
        doReturn(headers).when(userExcelImportSpy).extractHeaders(headerRow);
        doReturn(userDtoList).when(userExcelImportSpy).createUserDtoListFromSpreadsheet(rowIterator, headers);
        doReturn(inputStreamResource).when(userExcelImportSpy).generateResponse(any());

        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findAllActiveSupervisors()).thenReturn(supervisors);

        InputStreamResource result;
        try (MockedStatic<WorkbookFactory> mockedStatic = mockStatic(WorkbookFactory.class)) {
            mockedStatic.when(() -> WorkbookFactory.create(any(InputStream.class))).thenReturn(workbook);

            result = userExcelImportSpy.importUsers(file);
        }

        assertNotNull(result);
    }

    @Test
    public void extractHeadersTest() {
        Row headerRow = mock(Row.class);
        Cell cell1 = mock(Cell.class);
        Cell cell2 = mock(Cell.class);
        Cell cell3 = mock(Cell.class);
        Cell cell4 = mock(Cell.class);
        Cell cell5 = mock(Cell.class);
        Cell cell6 = mock(Cell.class);
        Cell cell7 = mock(Cell.class);
        Cell cell8 = mock(Cell.class);
        Cell cell9 = mock(Cell.class);
        Cell cell10 = mock(Cell.class);
        Cell cell11 = mock(Cell.class);
        Cell cell12 = mock(Cell.class);
        Cell cell13 = mock(Cell.class);
        Cell cell14 = mock(Cell.class);
        Cell cell15 = mock(Cell.class);
        Cell cell16 = mock(Cell.class);
        Cell cell17 = mock(Cell.class);

        when(headerRow.iterator()).thenReturn(List.of(cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9, cell10, cell11, cell12, cell13, cell14, cell15, cell16, cell17).iterator());
        when(cell1.getStringCellValue()).thenReturn("id pracownika");
        when(cell2.getStringCellValue()).thenReturn("imie");
        when(cell3.getStringCellValue()).thenReturn("nazwisko");
        when(cell4.getStringCellValue()).thenReturn("mail");
        when(cell5.getStringCellValue()).thenReturn("numer telefonu");
        when(cell6.getStringCellValue()).thenReturn("miasto");
        when(cell7.getStringCellValue()).thenReturn("ulica");
        when(cell8.getStringCellValue()).thenReturn("numer mieszkania");
        when(cell9.getStringCellValue()).thenReturn("kod pocztowy");
        when(cell10.getStringCellValue()).thenReturn("numer budynku");
        when(cell11.getStringCellValue()).thenReturn("role");
        when(cell12.getStringCellValue()).thenReturn("jezyki");
        when(cell13.getStringCellValue()).thenReturn("typ umowy");
        when(cell14.getStringCellValue()).thenReturn("podpisanie umowy");
        when(cell15.getStringCellValue()).thenReturn("wygasniecie umowy");
        when(cell16.getStringCellValue()).thenReturn("id przelozonego");
        when(cell17.getStringCellValue()).thenReturn("oddzial");

        List<String> result = userExcelImport.extractHeaders(headerRow);

        assertEquals(17, result.size());
    }

    @Test
    public void extractHeadersReturnNullTest() {
        Row headerRow = mock(Row.class);
        Cell cell = mock(Cell.class);
        when(cell.getStringCellValue()).thenReturn("header");
        when(headerRow.iterator()).thenReturn(List.of(cell).iterator());
        List<String> result = userExcelImport.extractHeaders(headerRow);

        assertNull(result);
    }

    @Test
    public void saveUsersTest() {
        List<UserDto> userDtoList = List.of(mock(UserDto.class));
        when(userService.create(any(UserDto.class))).thenReturn(mock(UserDto.class));

        List<UserDto> result = userExcelImport.saveUsers(userDtoList);

        assertNotNull(result);
    }

    @Test
    public void generateResponseTest() {
        List<UserDto> savedUsers = List.of(mock(UserDto.class));

        InputStreamResource result = userExcelImport.generateResponse(savedUsers);

        assertNotNull(result);
    }

    @Test
    public void createUserDtoListFromSpreadsheetTest() {
        Iterator<Row> rows = mock(Iterator.class);
        Row row = mock(Row.class);
        List<String> headers = List.of("id pracownika", "imie", "nazwisko", "mail",
                "numer telefonu", "miasto", "ulica", "numer mieszkania", "kod pocztowy", "numer budynku", "role", "jezyki",
                "typ umowy", "podpisanie umowy", "wygasniecie umowy", "id przelozonego", "oddzial");

        List<Role> roles = List.of(mock(Role.class));
        List<Language> languages = List.of(mock(Language.class));
        List<ContractType> contractTypes = List.of(mock(ContractType.class));
        List<User> supervisors = List.of(mock(User.class));
        List<Address> departments = List.of(mock(Address.class));

        when(repositoryCollector.getRoles()).thenReturn(roleRepository);
        when(repositoryCollector.getLanguages()).thenReturn(languageRepository);
        when(repositoryCollector.getContractTypes()).thenReturn(contractTypeRepository);
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(repositoryCollector.getAddresses()).thenReturn(addressRepository);

        when(roleRepository.findAll()).thenReturn(roles);
        when(languageRepository.findAll()).thenReturn(languages);
        when(contractTypeRepository.findAll()).thenReturn(contractTypes);
        when(userRepository.findAllActiveSupervisors()).thenReturn(supervisors);
        when(addressRepository.findByDepartmentNameNotNull()).thenReturn(departments);

        when(rows.hasNext()).thenReturn(true, false);
        when(rows.next()).thenReturn(row);
        when(row.getLastCellNum()).thenReturn((short) 17);

        UserExcelImport userExcelImportSpy = spy(userExcelImport);
        UserDto userDto = mock(UserDto.class);
        doReturn(userDto).when(userExcelImportSpy).createUserDtoFromRow(row, headers, roles, languages, contractTypes, supervisors, departments);

        List<UserDto> result = userExcelImportSpy.createUserDtoListFromSpreadsheet(rows, headers);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    public void createUserDtoFromRowTest() {
        Row row = mock(Row.class);
        Cell cell1 = mock(Cell.class);
        Cell cell2 = mock(Cell.class);
        Cell cell3 = mock(Cell.class);
        Cell cell4 = mock(Cell.class);
        Cell cell5 = mock(Cell.class);
        Cell cell6 = mock(Cell.class);
        Cell cell7 = mock(Cell.class);
        Cell cell8 = mock(Cell.class);
        Cell cell9 = mock(Cell.class);
        Cell cell10 = mock(Cell.class);
        Cell cell11 = mock(Cell.class);
        Cell cell12 = mock(Cell.class);
        Cell cell13 = mock(Cell.class);
        Cell cell14 = mock(Cell.class);
        Cell cell15 = mock(Cell.class);
        Cell cell16 = mock(Cell.class);
        Cell cell17 = mock(Cell.class);

        List<String> headers = List.of("id pracownika", "imie", "nazwisko", "mail",
                "numer telefonu", "miasto", "ulica", "numer mieszkania", "kod pocztowy", "numer budynku", "role", "jezyki",
                "typ umowy", "podpisanie umowy", "wygasniecie umowy", "id przelozonego", "oddzial");

        Role role = mock(Role.class);
        when(role.getName()).thenReturn("role1");
        Language language = mock(Language.class);
        when(language.getName()).thenReturn("language1");
        ContractType contractType = mock(ContractType.class);
        when(contractType.getName()).thenReturn("contractType1");
        User supervisor = mock(User.class);
        when(supervisor.getEmployeeId()).thenReturn("SUP01");
        Address address = mock(Address.class);
        when(address.getDepartmentName()).thenReturn("Department");

        List<Role> roles = List.of(role);
        List<Language> languages = List.of(language);
        List<ContractType> contractTypes = List.of(contractType);
        List<User> supervisors = List.of(supervisor);
        List<Address> departments = List.of(address);

        when(row.cellIterator()).thenReturn(List.of(cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9, cell10, cell11, cell12, cell13, cell14, cell15, cell16, cell17).iterator());
        when(cell1.getStringCellValue()).thenReturn("EMP01");
        when(cell2.getStringCellValue()).thenReturn("John");
        when(cell3.getStringCellValue()).thenReturn("Doe");
        when(cell4.getStringCellValue()).thenReturn("john.doe@example.com");
        when(cell5.getStringCellValue()).thenReturn("123456789");
        when(cell6.getStringCellValue()).thenReturn("City");
        when(cell7.getStringCellValue()).thenReturn("Street");
        when(cell8.getStringCellValue()).thenReturn("10");
        when(cell9.getStringCellValue()).thenReturn("12-345");
        when(cell10.getStringCellValue()).thenReturn("1");
        when(cell11.getStringCellValue()).thenReturn("role1");
        when(cell12.getStringCellValue()).thenReturn("language1");
        when(cell13.getStringCellValue()).thenReturn("contractType1");
        when(cell14.getStringCellValue()).thenReturn("2023-01-01");
        when(cell15.getStringCellValue()).thenReturn("2023-12-31");
        when(cell16.getStringCellValue()).thenReturn("SUP01");
        when(cell17.getStringCellValue()).thenReturn("Department");

        when(cell1.getColumnIndex()).thenReturn(0);
        when(cell2.getColumnIndex()).thenReturn(1);
        when(cell3.getColumnIndex()).thenReturn(2);
        when(cell4.getColumnIndex()).thenReturn(3);
        when(cell5.getColumnIndex()).thenReturn(4);
        when(cell6.getColumnIndex()).thenReturn(5);
        when(cell7.getColumnIndex()).thenReturn(6);
        when(cell8.getColumnIndex()).thenReturn(7);
        when(cell9.getColumnIndex()).thenReturn(8);
        when(cell10.getColumnIndex()).thenReturn(9);
        when(cell11.getColumnIndex()).thenReturn(10);
        when(cell12.getColumnIndex()).thenReturn(11);
        when(cell13.getColumnIndex()).thenReturn(12);
        when(cell14.getColumnIndex()).thenReturn(13);
        when(cell15.getColumnIndex()).thenReturn(14);
        when(cell16.getColumnIndex()).thenReturn(15);
        when(cell17.getColumnIndex()).thenReturn(16);

        UserDto result = userExcelImport.createUserDtoFromRow(row, headers, roles, languages, contractTypes, supervisors, departments);

        assertNotNull(result);
        assertEquals("EMP01", result.employeeId());
        assertEquals("John", result.firstname());
        assertEquals("Doe", result.surname());
        assertEquals("john.doe@example.com", result.email());
        assertEquals("123456789", result.phoneNumber());
        assertNotNull(result.residence());
        assertNotNull(result.roles());
        assertNotNull(result.languages());
        assertNotNull(result.contractType());
        assertNotNull(result.contractSignature());
        assertNotNull(result.contractExpiration());
        assertNotNull(result.supervisorId());
        assertNotNull(result.workAddress());
    }

    @Test
    public void updateSupervisorsBypassIfStatementTest() {
        Sheet sheet = mock(Sheet.class);
        Row row = mock(Row.class);
        Cell cell = mock(Cell.class);
        List<String> headers = List.of("id pracownika", "imie", "nazwisko", "mail",
                "numer telefonu", "miasto", "ulica", "numer mieszkania", "kod pocztowy", "numer budynku", "role", "jezyki",
                "typ umowy", "podpisanie umowy", "wygasniecie umowy", "id przelozonego", "oddzial");

        List<UserDto> savedUsers = List.of(
                UserDto.builder().id(1L).supervisorId(null).build(),
                UserDto.builder().id(2L).supervisorId(null).build()
        );

        List<User> supervisors = List.of(
                User.builder().employeeId("SUP01").build(),
                User.builder().employeeId("SUP02").build()
        );

        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findAllActiveSupervisors()).thenReturn(supervisors);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(sheet.getRow(1)).thenReturn(row);
        when(sheet.getRow(2)).thenReturn(row);
        when(row.getCell(headers.indexOf("id przelozonego"))).thenReturn(cell);
        when(cell.getStringCellValue()).thenReturn("SUP01", "SUP02");

        userExcelImport.updateSupervisors(sheet, headers, savedUsers);

        verify(user1).setSupervisor(supervisors.get(0));
        verify(user2).setSupervisor(supervisors.get(1));
        verify(repositoryCollector.getUsers(), times(1)).saveAll(anyList());
    }
}