package org.harmoniapp.services.importexport;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.harmoniapp.contracts.profile.AddressDto;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.util.List;

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
    public void createResponseListTest() {
        List<UserDto> savedUsers = List.of(mock(UserDto.class));

        List<UserDto> result = userExcelImport.createResponseList(savedUsers);

        assertNotNull(result);
    }

    @Test
    public void generatePdfTest() {
        List<UserDto> response = List.of(mock(UserDto.class));

        byte[] result = userExcelImport.generatePdf(response);

        assertNotNull(result);
    }

    @Test
    public void createPdfTableTest() {
        List<UserDto> response = List.of(mock(UserDto.class));
        Font font = mock(Font.class);

        assertDoesNotThrow(() -> userExcelImport.createPdfTable(response, font));
    }

    @Test
    public void addTableCellTest() {
        PdfPTable table = mock(PdfPTable.class);
        Font font = mock(Font.class);

        assertDoesNotThrow(() -> userExcelImport.addTableCell(table, "text", font));
    }

    @Test
    public void populateUserAndAddressBuildersTest() {
        Cell cell = mock(Cell.class);
        String header = "header";
        UserDto.UserDtoBuilder userBuilder = UserDto.builder();
        AddressDto.AddressDtoBuilder addressBuilder = AddressDto.builder();
        List<Role> roles = List.of(mock(Role.class));
        List<Language> languages = List.of(mock(Language.class));
        List<ContractType> contractTypes = List.of(mock(ContractType.class));
        List<User> supervisors = List.of(mock(User.class));
        List<Address> departments = List.of(mock(Address.class));

        assertDoesNotThrow(() -> userExcelImport.populateUserAndAddressBuilders(cell, header, userBuilder, addressBuilder, roles, languages, contractTypes, supervisors, departments));
    }
}