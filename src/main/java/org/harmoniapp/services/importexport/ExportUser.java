package org.harmoniapp.services.importexport;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

/**
 * Interface for exporting user data.
 */
public interface ExportUser {

    /**
     * Exports user data as an InputStreamResource wrapped in a ResponseEntity.
     *
     * @return ResponseEntity containing the InputStreamResource of exported user data.
     */
    ResponseEntity<InputStreamResource> exportUsers();
}
