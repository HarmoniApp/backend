package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.harmoniapp.harmonidata.entities.Absence;
import org.harmoniapp.harmonidata.entities.AbsenceType;
import org.harmoniapp.harmonidata.entities.Status;
import org.harmoniapp.harmonidata.entities.User;

import java.time.LocalDate;

/**
 * Data Transfer Object for Absence.
 *
 * @param id              the unique identifier of the absence
 * @param start           the start time of the absence
 * @param end             the end time of the absence
 * @param userId          the ID of the user associated with the absence
 * @param absenceTypeId   the ID of the absence type
 * @param status          the status object
 * @param submission      the submission date of the absence
 * @param updated         the last updated date of the absence
 */
public record AbsenceDto(
        long id,

        @NotNull(message = "Start date cannot be null")
        @FutureOrPresent(message = "Start date must be in the future or present")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate start,

        @NotNull(message = "End date cannot be null")
        @FutureOrPresent(message = "End date must be in the future or present")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate end,

        @NotNull(message = "User ID cannot be null")
        @Digits(integer = 6, fraction = 0, message = "User ID must be a valid number with up to 6 digits")
        @JsonProperty("user_id") Long userId,

        @NotNull(message = "Absence type ID cannot be null")
        @Digits(integer = 6, fraction = 0, message = "Absence type ID must be a valid number with up to 6 digits")
        @JsonProperty("absence_type_id") Long absenceTypeId,

        Status status,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate submission,

        LocalDate updated,

        @JsonProperty("working_days") Long workingDays) {

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
                absence.getStatus(),
                absence.getSubmission(),
                absence.getUpdated(),
                absence.getWorkingDays()
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
                this.updated,
                this.workingDays
        );
    }
}

