package org.harmoniapp.services.importexport;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for importing users.
 */
public interface ImportUser {

    /**
     * Imports users from a given file.
     *
     * @param file the file containing user data
     * @return a ResponseEntity containing an InputStreamResource
     */
    ResponseEntity<InputStreamResource> importUsers(MultipartFile file);
}
