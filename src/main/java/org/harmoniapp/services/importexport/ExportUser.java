package org.harmoniapp.services.importexport;

import org.springframework.core.io.InputStreamResource;

/**
 * Interface for exporting user data.
 */
public interface ExportUser {

    /**
     * Exports user data as an InputStreamResource wrapped in a ResponseEntity.
     *
     * @return an InputStreamResource of exported user data.
     */
    InputStreamResource exportUsers();
}
