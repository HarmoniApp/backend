package org.harmoniapp.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.entities.ArchivedShift;

/**
 * Data Transfer Object for ArchivedShift.
 * Contains only the necessary fields for external representation.
 */
public record ArchivedShiftDto(
        long id,
        @JsonProperty("file_title") String fileTitle) {

    /**
     * Converts an ArchivedShift entity into an ArchivedShiftDto.
     *
     * @param archivedShift the ArchivedShift entity to convert
     * @return a new ArchivedShiftDto with the ID and file title of the archived shift
     */
    public static ArchivedShiftDto fromEntity(ArchivedShift archivedShift) {
        return new ArchivedShiftDto(
                archivedShift.getId(),
                archivedShift.getFileTitle());
    }
}
