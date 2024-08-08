package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.Absence;
import org.harmoniapp.harmonidata.entities.AbsenceType;
import org.harmoniapp.harmonidata.entities.Status;
import org.harmoniapp.harmonidata.entities.User;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Data Transfer Object for Absence.
 *
 * @param id              the unique identifier of the absence
 * @param start           the start time of the absence
 * @param end             the end time of the absence
 * @param userId          the ID of the user associated with the absence
 * @param absenceTypeId   the ID of the absence type
 * @param statusId        the ID of the status
 * @param submission      the submission date of the absence
 * @param updated         the last updated date of the absence
 */
public record AbsenceDto(
        long id,
        LocalDateTime start,
        LocalDateTime end,
        @JsonProperty("user_id") Long userId,
        @JsonProperty("absence_type_id") Long absenceTypeId,
        @JsonProperty("status_id") Long statusId,
        Date submission,
        Date updated) {

    /**
     * Converts an Absence entity to an AbsenceDto.
     *
     * @param absence the Absence entity to convert
     * @return the resulting AbsenceDto
     */
    public static AbsenceDto fromEntity(Absence absence) {
        return new AbsenceDto(
                absence.getId(),
                absence.getStart(),
                absence.getEnd(),
                absence.getUser().getId(),
                absence.getAbsenceType().getId(),
                absence.getStatus().getId(),
                absence.getSubmission(),
                absence.getUpdated()
        );
    }

    /**
     * Converts an AbsenceDto to an Absence entity.
     *
     * @param user the User entity associated with the absence
     * @param absenceType the AbsenceType entity associated with the absence
     * @param status the Status entity associated with the absence
     * @return the resulting Absence entity
     */
    public Absence toEntity(User user, AbsenceType absenceType, Status status) {
        return new Absence(
                this.id,
                this.start,
                this.end,
                user,
                absenceType,
                status,
                this.submission,
                this.updated
        );
    }
}

