package org.harmoniapp.harmoniwebapi.services.importexport;

import org.harmoniapp.harmoniwebapi.contracts.UserImportResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ImportUser {
    ResponseEntity<UserImportResponseDto> importUsers(MultipartFile file);
}
