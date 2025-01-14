package org.harmoniapp.controllers.importexport;

import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.services.auth.LoginService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Test class for {@link ImportController} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ImportControllerIT {

    private final MockMvc mockMvc;
    private static String jwtAdmin;

    @Autowired
    public ImportControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public static void setUp(@Autowired LoginService loginService) {
        // Login as an admin to get a JWT token
        var credentialsAdmin = new LoginRequestDto("jan.kowalski@example.com", "StrongPassword!2137");
        jwtAdmin = loginService.login(credentialsAdmin).jwtToken();
    }

    @Test
    public void importUsersFromExcelTest() throws Exception {
        File photo = new File("src/test/resources/testFiles/employees/valid.xlsx");
        InputStream inputStream = new FileInputStream(photo);
        MockMultipartFile mockFile = new MockMultipartFile("file", "valid.xlsx", "multipart/form-data", inputStream);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/excel/users/import-excel")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    public void importUsersFromExcelMissingHeadersTest() throws Exception {
        File photo = new File("src/test/resources/testFiles/employees/headers_miss.xlsx");
        InputStream inputStream = new FileInputStream(photo);
        MockMultipartFile mockFile = new MockMultipartFile("file", "headers_miss.xlsx", "multipart/form-data", inputStream);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/excel/users/import-excel")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void importUsersFromExcelMissingCellsTest() throws Exception {
        File photo = new File("src/test/resources/testFiles/employees/cell_miss.xlsx");
        InputStream inputStream = new FileInputStream(photo);
        MockMultipartFile mockFile = new MockMultipartFile("file", "cell_miss.xlsx", "multipart/form-data", inputStream);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/excel/users/import-excel")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void importUsersFromExcelInvalidFileFormatTest() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "largeFile.exe", "application/octet-stream", "dummy content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/excel/users/import-excel")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void importScheduleFromExcelTest() throws Exception {
        File photo = new File("src/test/resources/testFiles/shifts/valid.xlsx");
        InputStream inputStream = new FileInputStream(photo);
        MockMultipartFile mockFile = new MockMultipartFile("file", "valid.xlsx", "multipart/form-data", inputStream);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/excel/shifts/import-excel")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void importScheduleFromExcelMissingHeaderTest() throws Exception {
        File photo = new File("src/test/resources/testFiles/shifts/header_miss.xlsx");
        InputStream inputStream = new FileInputStream(photo);
        MockMultipartFile mockFile = new MockMultipartFile("file", "header_miss.xlsx", "multipart/form-data", inputStream);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/excel/shifts/import-excel")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void importScheduleFromExcelInvalidDateTest() throws Exception {
        File photo = new File("src/test/resources/testFiles/shifts/invalid_date.xlsx");
        InputStream inputStream = new FileInputStream(photo);
        MockMultipartFile mockFile = new MockMultipartFile("file", "invalid_date.xlsx", "multipart/form-data", inputStream);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/excel/shifts/import-excel")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .header("Authorization", "Bearer " + jwtAdmin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
