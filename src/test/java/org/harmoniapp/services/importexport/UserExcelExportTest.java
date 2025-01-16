package org.harmoniapp.services.importexport;

import org.harmoniapp.contracts.profile.AddressDto;
import org.harmoniapp.contracts.profile.ContractTypeDto;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.contracts.user.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserExcelExportTest {

    @Mock
    private UserDataService userDataService;

    @InjectMocks
    private UserExcelExport userExcelExport;

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

        InputStreamResource result = userExcelExport.exportUsers();

        assertNotNull(result);
    }
}