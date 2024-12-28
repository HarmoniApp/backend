package org.harmoniapp.contracts.absence;

import org.harmoniapp.entities.absence.AbsenceType;

/**
 * Data Transfer Object for AbsenceType.
 *
 * @param id   the unique identifier of the AbsenceType
 * @param name the name of the AbsenceType
 */
public record AbsenceTypeDto(long id, String name) {

    /**
     * Converts a AbsenceType entity to a AbsenceTypeDto.
     *
     * @param absenceType the AbsenceType entity to convert
     * @return the resulting AbsenceTypeDto
     */
    public static AbsenceTypeDto fromEntity(AbsenceType absenceType) {
        return new AbsenceTypeDto(absenceType.getId(), absenceType.getName());
    }

    /**
     * Converts a AbsenceTypeDto to a AbsenceType entity.
     *
     * @return the resulting AbsenceType entity
     */
    public AbsenceType toEntity() {
        return new AbsenceType(this.id, this.name);
    }
}
