package org.harmoniapp.services.importexport;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for importing users.
 */
public interface ImportUser {

    /**
     * Imports users from a given file.
     *
     * @param file the file containing user data
     * @return an InputStreamResource containing the result of the import operation
     */
    InputStreamResource importUsers(MultipartFile file);
}
