package org.harmoniapp.harmoniwebapi.contracts;

import org.harmoniapp.harmonidata.entities.Absence;

import java.time.LocalDate;

/**
 * Partial Data Transfer Object (DTO) for Absence.
 *
 * @param id    the unique identifier of the absence
 * @param start the start date of the absence
 * @param end   the end date of the absence
 */
public record PartialAbsenceDto(Long id, LocalDate start, LocalDate end) {

    /**
     * Converts an Absence entity to a PartialAbsenceDto.
     *
     * @param absence the Absence entity to convert
     * @return the resulting PartialAbsenceDto
     */
    public static PartialAbsenceDto fromEntity(Absence absence){
        return new PartialAbsenceDto(
                absence.getId(),
                absence.getStart(),
                absence.getEnd()
        );
    }
}
