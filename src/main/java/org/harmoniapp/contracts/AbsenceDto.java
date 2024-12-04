package org.harmoniapp.contracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.harmoniapp.entities.Absence;
import org.harmoniapp.entities.AbsenceType;
import org.harmoniapp.entities.Status;
import org.harmoniapp.entities.User;

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
        @Positive(message = "User ID must be a positive number")
        @JsonProperty("user_id") Long userId,

        @NotNull(message = "Absence type ID cannot be null")
        @Positive(message = "Absence ID must be a positive number")
        @JsonProperty("absence_type_id") Long absenceTypeId,

        Status status,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate submission,

        LocalDate updated,

        @JsonProperty("working_days") Long workingDays,

        boolean archived,

        @JsonProperty("employee_id") String employeeId) {

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
                absence.getWorkingDays(),
                absence.getArchived(),
                absence.getUser().getEmployeeId()
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
                this.workingDays,
                this.archived
        );
    }
}

