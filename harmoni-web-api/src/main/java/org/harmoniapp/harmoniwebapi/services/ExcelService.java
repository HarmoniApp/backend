package org.harmoniapp.harmoniwebapi.services;


import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.ShiftDto;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class ExcelService {
    private final RepositoryCollector repositoryCollector;

    public ResponseEntity<InputStreamResource> exportUsersToExcel() {
        List<UserDto> users = repositoryCollector.getUsers().findAll().stream()
                .map(UserDto::fromEntity)
                .sorted(Comparator.comparing(UserDto::surname))
                .toList();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Employee ID");
            headerRow.createCell(1).setCellValue("First Name");
            headerRow.createCell(2).setCellValue("Surname");
            headerRow.createCell(3).setCellValue("Email");

            int rowIdx = 1;
            for (UserDto user : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(user.employeeId());
                row.createCell(1).setCellValue(user.firstname());
                row.createCell(2).setCellValue(user.surname());
                row.createCell(3).setCellValue(user.email());
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
