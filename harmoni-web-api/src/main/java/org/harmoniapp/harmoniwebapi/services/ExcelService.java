package org.harmoniapp.harmoniwebapi.services;


import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
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
import java.util.Comparator;
import java.util.List;

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
}
