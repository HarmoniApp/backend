package org.harmoniapp.harmoniwebapi.services.importexport;

import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImportUser {
    ResponseEntity<List<UserDto>> importUsers(MultipartFile file);
}
