package org.harmoniapp.harmoniwebapi.services.importexport;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ImportSchedule {
    ResponseEntity<String> importSchedule(MultipartFile file);
}
