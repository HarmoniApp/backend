package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.ArchivedShift;

public record ArchivedShiftDto(
        long id,
        @JsonProperty("file_title") String fileTitle) {

    public static ArchivedShiftDto fromEntity(ArchivedShift archivedShift) {
        return new ArchivedShiftDto(
                archivedShift.getId(),
                archivedShift.getFileTitle());
    }
}
