package org.harmoniapp.harmoniwebapi.services.importexport;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ImportUser {
    ResponseEntity<InputStreamResource> importUsers(MultipartFile file);
}
