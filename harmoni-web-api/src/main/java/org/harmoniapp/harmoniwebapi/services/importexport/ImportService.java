package org.harmoniapp.harmoniwebapi.services.importexport;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.harmoniapp.harmonidata.entities.*;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.AddressDto;
import org.harmoniapp.harmoniwebapi.contracts.LanguageDto;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.harmoniapp.harmoniwebapi.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportService {
    private final RepositoryCollector repositoryCollector;
    private final UserService userService;
    private final List<String> expectedHeaders = List.of("employee id", "first name", "surname", "email",
            "phone number", "city", "street", "apartment", "zip code", "building number", "roles", "languages",
            "contract type", "contract signature", "contract expiration", "supervisor employee id", "department name");

    public ResponseEntity<String> importUsersFromExcel(MultipartFile file) {
        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                return ResponseEntity.badRequest().body("Sheet not found");
            }
            Iterator<Row> rows = sheet.rowIterator();
            if (!rows.hasNext()) {
                return ResponseEntity.badRequest().body("No rows found");
            }
            Row headerRow = rows.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().toLowerCase());
            }
            if (new HashSet<>(headers).size() != headers.size()) {
                return ResponseEntity.badRequest().body("Duplicate headers");
            }
            if (!new HashSet<>(headers).containsAll(expectedHeaders)) {
                return ResponseEntity.badRequest().body("Invalid headers");
            }

            List<UserDto> userDtoList;
            try {
                userDtoList = createUserDtoListFromSpreadsheet(rows, headers);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body("Invalid row: " + e.getMessage());
            }
            List<UserDto> savedUsers = new ArrayList<>();
            //TODO: Failed row should stop the import?
            List<Integer> failedRows = new ArrayList<>();
            for (int i = 0; i < userDtoList.size(); i++) {
                UserDto newUser = userService.add(userDtoList.get(i));
                try {
                    savedUsers.add(newUser);
                } catch (Exception e) {
                    savedUsers.add(null);
                    failedRows.add(i);
                }
            }
            List<User> supervisors = repositoryCollector.getUsers().findByRoles_IsSupTrue();
            List<User> usersToUpdate = new ArrayList<>();
            for (int i = 0; i < savedUsers.size(); i++) {
                UserDto userDto = savedUsers.get(i);
                if (userDto == null || userDto.supervisorId() != null) {
                    continue;
                }
                User user = repositoryCollector.getUsers().findById(userDto.id()).orElseThrow();
                var sup = sheet.getRow(i+1).getCell(headers.indexOf("supervisor employee id"));
                user.setSupervisor(supervisors.stream()
                        .filter(s -> s.getEmployeeId().equals(sup.getStringCellValue()))
                        .findFirst().orElse(null));

                usersToUpdate.add(user);
            }
            repositoryCollector.getUsers().saveAll(usersToUpdate);
            if (failedRows.isEmpty()) {
                return ResponseEntity.ok("Imported users from excel");
            } else {
                return ResponseEntity.ok().body("Imported users from excel, but some rows failed: " + failedRows);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error reading file");
        }

        return ResponseEntity.badRequest().body("Error importing users from excel");
    }

    private List<UserDto> createUserDtoListFromSpreadsheet(Iterator<Row> rows, List<String> headers) throws RuntimeException {
        List<Role> roles = repositoryCollector.getRoles().findAll();
        List<Language> languages = repositoryCollector.getLanguages().findAll();
        List<ContractType> contractTypes = repositoryCollector.getContractTypes().findAll();
        List<User> supervisors = repositoryCollector.getUsers().findByRoles_IsSupTrue();
        List<Address> departments = repositoryCollector.getAddresses().findByDepartmentNameNotNull();
        List<UserDto> userDtoList = new ArrayList<>();

        while (rows.hasNext()) {
            Row row = rows.next();
            System.out.println(row.getRowNum());
            validateRow(row);
            UserDto userDto = createUserDtoFromRow(row, headers, roles, languages, contractTypes, supervisors, departments);
            userDtoList.add(userDto);
        }

        return userDtoList;
    }

    private void validateRow(Row row) {
        if (row.getLastCellNum() != expectedHeaders.size()) {
            throw new RuntimeException("Invalid row: " + row.getRowNum());
        }
    }

    private UserDto createUserDtoFromRow(Row row, List<String> headers, List<Role> roles, List<Language> languages,
                                         List<ContractType> contractTypes, List<User> supervisors, List<Address> departments) {
        Iterator<Cell> cells = row.cellIterator();
        var userBuilder = UserDto.builder();
        var addressBuilder = AddressDto.builder();

        while (cells.hasNext()) {
            Cell cell = cells.next();
            populateUserAndAddressBuilders(cell, headers.get(cell.getColumnIndex()), userBuilder, addressBuilder, roles, languages, contractTypes, supervisors, departments);
        }

        return userBuilder.residence(addressBuilder.build()).build();
    }

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
            case "supervisor employee id" -> userBuilder.supervisorId(getSupervisorId(cell.getStringCellValue(), supervisors));
            case "department name" -> userBuilder.workAddress(getDepartment(cell.getStringCellValue(), departments));
        }
    }

    private List<Role> getRoles(String roles, List<Role> roleList) {
        return roleList.stream()
                .filter(r -> List.of(roles.split(",")).contains(r.getName()))
                .toList();
    }

    private List<LanguageDto> getLanguages(String languages, List<Language> languageList) {
        return languageList.stream()
                .filter(l -> List.of(languages.split(",")).contains(l.getName()))
                .map(LanguageDto::fromEntity)
                .toList();
    }

    private ContractType getContractType(String contractTypeName, List<ContractType> contractTypes) {
        return contractTypes.stream()
                .filter(c -> c.getName().equals(contractTypeName))
                .findFirst()
                .orElse(null);
    }

    private Long getSupervisorId(String supervisorEmployeeId, List<User> supervisors) {
        return supervisors.stream()
                .filter(u -> u.getEmployeeId().equals(supervisorEmployeeId))
                .findFirst()
                .map(User::getId)
                .orElse(-1L);
    }

    private AddressDto getDepartment(String departmentName, List<Address> departments) {
        return departments.stream()
                .filter(a -> a.getDepartmentName().equals(departmentName))
                .map(AddressDto::fromEntity)
                .findFirst()
                .orElse(null);
    }
}
