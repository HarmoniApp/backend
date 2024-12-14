package org.harmoniapp.services.importexport;


import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.contracts.schedule.ShiftDto;
import org.harmoniapp.contracts.user.UserDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelService {
    private final RepositoryCollector repositoryCollector;

    public ResponseEntity<InputStreamResource> exportUsersToExcel() {
        List<UserDto> users = repositoryCollector.getUsers().findAll().stream()
                .map(UserDto::fromEntity)
                .sorted(Comparator.comparing(UserDto::surname))
                .toList();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            String[] headersCell = {"Employee ID", "First Name", "Surname", "Email", "Phone number", "City", "Street",
                    "Apartment", "Zip code", "Building number", "Roles", "Languages",
                    "Contract type", "Contract signature", "Contract expiration",
                    "Supervisor employee ID", "Department name"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headersCell.length; i++) {
                headerRow.createCell(i).setCellValue(headersCell[i]);
            }

            int rowIdx = 1;
            for (UserDto user : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(user.employeeId());
                row.createCell(1).setCellValue(user.firstname());
                row.createCell(2).setCellValue(user.surname());
                row.createCell(3).setCellValue(user.email());
                row.createCell(4).setCellValue(user.phoneNumber());
                row.createCell(5).setCellValue(user.residence().city());
                row.createCell(6).setCellValue(user.residence().street());
                row.createCell(7).setCellValue(user.residence().apartment());
                row.createCell(8).setCellValue(user.residence().zipCode());
                row.createCell(9).setCellValue(user.residence().buildingNumber());
                row.createCell(10).setCellValue(
                        user.roles().stream()
                                .map(RoleDto::name)
                                .collect(Collectors.joining(", "))
                );
                row.createCell(11).setCellValue(
                        user.languages().stream()
                                .map(LanguageDto::name)
                                .collect(Collectors.joining(", "))
                );
                row.createCell(12).setCellValue(user.contractType().getName());
                row.createCell(13).setCellValue(user.contractSignature().toString());
                row.createCell(14).setCellValue(user.contractExpiration().toString());
                String supervisorEmployeeId = repositoryCollector.getUsers()
                        .findById(user.supervisorId())
                        .map(User::getEmployeeId)
                        .orElse("");

                row.createCell(15).setCellValue(supervisorEmployeeId);
                row.createCell(16).setCellValue(user.workAddress().departmentName());
            }

            for (int i = 0; i < headersCell.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=users.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(new InputStreamResource(in));
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to Excel", e);
        }
    }

    public ResponseEntity<InputStreamResource> exportShiftsToExcel(LocalDate startDate, LocalDate endDate) {
        List<ShiftDto> shifts = repositoryCollector.getShifts().findAllByDateRange(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)).stream()
                .map(ShiftDto::fromEntity)
                .toList();

        List<UserDto> users = shifts.stream()
                .map(shift -> repositoryCollector.getUsers().findById(shift.userId()).map(UserDto::fromEntity).orElse(null))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, Map<LocalDate, String>> userShiftMap = new HashMap<>();
        for (ShiftDto shift : shifts) {
            userShiftMap.computeIfAbsent(shift.userId(), k -> new HashMap<>())
                    .put(shift.start().toLocalDate(), shift.start().toLocalTime() + " - " + shift.end().toLocalTime());
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Shifts");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Employee ID");

            LocalDate current = startDate;
            int colIdx = 1;
            while (!current.isAfter(endDate)) {
                headerRow.createCell(colIdx++).setCellValue(current.toString());
                current = current.plusDays(1);
            }

            int rowIdx = 1;
            for (UserDto user : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(user.employeeId());

                current = startDate;
                colIdx = 1;
                Map<LocalDate, String> shiftsForUser = userShiftMap.getOrDefault(user.id(), new HashMap<>());
                while (!current.isAfter(endDate)) {
                    String shiftTime = shiftsForUser.get(current);
                    if (shiftTime != null) {
                        row.createCell(colIdx).setCellValue(shiftTime);
                    } else {
                        row.createCell(colIdx).setCellValue("");
                    }
                    colIdx++;
                    current = current.plusDays(1);
                }
            }

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=shifts.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(new InputStreamResource(in));
        } catch (IOException e) {
            throw new RuntimeException("Failed to export shifts to Excel", e);
        }
    }
}
