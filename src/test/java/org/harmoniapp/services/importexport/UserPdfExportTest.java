package org.harmoniapp.services.importexport;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;
import org.harmoniapp.contracts.profile.AddressDto;
import org.harmoniapp.contracts.profile.ContractTypeDto;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.exception.FileGenerationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPdfExportTest {

    @Mock
    private UserDataService userDataService;

    @InjectMocks
    private UserPdfExport userPdfExport;

    @Test
    public void exportUsersTest() {
        LocalDate now = LocalDate.now();
        AddressDto residence = AddressDto.builder()
                .city("city")
                .street("street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .build();
        AddressDto workAddress = AddressDto.builder()
                .city("city")
                .street("street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .departmentName("department")
                .build();
        ContractTypeDto contractTypeDto = new ContractTypeDto(1L, "contractType", 2);
        List<RoleDto> roleDtos = List.of(new RoleDto(1L, "role", "#000000"));
        List<LanguageDto> languageDtos = List.of(new LanguageDto(1L, "English", "en"));
        UserDto user = UserDto.builder()
                .id(1L)
                .firstname("firstname")
                .surname("surname")
                .email("email@example.com")
                .contractType(contractTypeDto)
                .contractSignature(now.plusDays(1))
                .contractExpiration(now.plusYears(1))
                .residence(residence)
                .workAddress(workAddress)
                .supervisorId(2L)
                .photo("123456789")
                .employeeId("EMP01")
                .roles(roleDtos)
                .languages(languageDtos)
                .build();
        List<UserDto> users = List.of(user);
        when(userDataService.getAllUsers()).thenReturn(users);

        InputStreamResource result = userPdfExport.exportUsers();

        assertNotNull(result);
    }

    @Test
    public void writeDocumentTest() {
        Document document = mock(Document.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LocalDate now = LocalDate.now();
        AddressDto residence = AddressDto.builder()
                .city("city")
                .street("street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .build();
        AddressDto workAddress = AddressDto.builder()
                .city("city")
                .street("street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .departmentName("department")
                .build();
        ContractTypeDto contractTypeDto = new ContractTypeDto(1L, "contractType", 2);
        List<RoleDto> roleDtos = List.of(new RoleDto(1L, "role", "#000000"));
        List<LanguageDto> languageDtos = List.of(new LanguageDto(1L, "English", "en"));
        UserDto user = UserDto.builder()
                .id(1L)
                .firstname("firstname")
                .surname("surname")
                .email("email@example.com")
                .contractType(contractTypeDto)
                .contractSignature(now.plusDays(1))
                .contractExpiration(now.plusYears(1))
                .residence(residence)
                .workAddress(workAddress)
                .supervisorId(2L)
                .photo("123456789")
                .employeeId("EMP01")
                .roles(roleDtos)
                .languages(languageDtos)
                .build();
        List<UserDto> users = List.of(user);

        assertDoesNotThrow(() -> userPdfExport.writeDocument(document, out, users));
    }

    @Test
    public void writeDocumentExceptionTest() {
        Document document = mock(Document.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<UserDto> users = List.of(mock(UserDto.class));
        doThrow(DocumentException.class).when(document).open();

        assertThrows(FileGenerationException.class, () -> userPdfExport.writeDocument(document, out, users));
    }

    @Test
    public void addTitleTest() {
        Document document = mock(Document.class);

        assertDoesNotThrow(() -> userPdfExport.addTitle(document));
    }

    @Test
    public void addTableTest() {
        Document document = mock(Document.class);
        LocalDate now = LocalDate.now();
        AddressDto residence = AddressDto.builder()
                .city("city")
                .street("street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .build();
        AddressDto workAddress = AddressDto.builder()
                .city("city")
                .street("street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .departmentName("department")
                .build();
        ContractTypeDto contractTypeDto = new ContractTypeDto(1L, "contractType", 2);
        List<RoleDto> roleDtos = List.of(new RoleDto(1L, "role", "#000000"));
        List<LanguageDto> languageDtos = List.of(new LanguageDto(1L, "English", "en"));
        UserDto user = UserDto.builder()
                .id(1L)
                .firstname("firstname")
                .surname("surname")
                .email("email@example.com")
                .contractType(contractTypeDto)
                .contractSignature(now.plusDays(1))
                .contractExpiration(now.plusYears(1))
                .residence(residence)
                .workAddress(workAddress)
                .supervisorId(2L)
                .photo("123456789")
                .employeeId("EMP01")
                .roles(roleDtos)
                .languages(languageDtos)
                .build();
        List<UserDto> users = List.of(user);

        assertDoesNotThrow(() -> userPdfExport.addTable(document, users));
    }

    @Test
    public void addRowsTest() {
        PdfPTable table = mock(PdfPTable.class);
        LocalDate now = LocalDate.now();
        AddressDto residence = AddressDto.builder()
                .city("city")
                .street("street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .build();
        AddressDto workAddress = AddressDto.builder()
                .city("city")
                .street("street")
                .buildingNumber("1")
                .apartment("1")
                .zipCode("00-000")
                .departmentName("department")
                .build();
        ContractTypeDto contractTypeDto = new ContractTypeDto(1L, "contractType", 2);
        List<RoleDto> roleDtos = List.of(new RoleDto(1L, "role", "#000000"));
        List<LanguageDto> languageDtos = List.of(new LanguageDto(1L, "English", "en"));
        UserDto user = UserDto.builder()
                .id(1L)
                .firstname("firstname")
                .surname("surname")
                .email("email@example.com")
                .contractType(contractTypeDto)
                .contractSignature(now.plusDays(1))
                .contractExpiration(now.plusYears(1))
                .residence(residence)
                .workAddress(workAddress)
                .supervisorId(2L)
                .photo("123456789")
                .employeeId("EMP01")
                .roles(roleDtos)
                .languages(languageDtos)
                .build();
        List<UserDto> users = List.of(user);

        assertDoesNotThrow(() -> userPdfExport.addRows(table, users));
    }
}