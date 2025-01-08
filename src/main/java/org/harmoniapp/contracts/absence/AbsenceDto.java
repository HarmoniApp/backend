package org.harmoniapp.contracts.absence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.absence.AbsenceType;
import org.harmoniapp.entities.absence.Status;
import org.harmoniapp.entities.user.User;

import java.time.LocalDate;

/**
 * Data Transfer Object for Absence.
 *
 * @param id            the unique identifier of the absence
 * @param start         the start time of the absence
 * @param end           the end time of the absence
 * @param userId        the ID of the user associated with the absence
 * @param absenceTypeId the ID of the absence type
 * @param status        the status object
 * @param submission    the submission date of the absence
 * @param updated       the last updated date of the absence
 * @param workingDays   the number of working days of the absence
 * @param employeeId    the employee ID of the user associated with the absence
 */
@Builder
public record AbsenceDto(
        long id,

        @NotNull(message = "Data początkowa nie może być pusta")
        @FutureOrPresent(message = "Data początkowa musi być w przyszłości lub teraźniejszości")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate start,

        @NotNull(message = "Data końcowa nie może być pusta")
        @FutureOrPresent(message = "Data końcowa musi być w przyszłości lub teraźniejszości")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate end,

        @NotNull(message = "ID użytkownika nie może być puste")
        @Positive(message = "ID użytkownika musi być liczbą dodatnią")
        @JsonProperty("user_id") Long userId,

        @NotNull(message = "ID typu nieobecności nie może być puste")
        @Positive(message = "ID typu nieobecności musi być liczbą dodatnią")
        @JsonProperty("absence_type_id") Long absenceTypeId,

        Status status,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate submission,

        LocalDate updated,

        @JsonProperty("working_days") Long workingDays,

        @JsonProperty("employee_id") String employeeId) {

    /**
     * Converts an Absence entity to an AbsenceDto.
     *
     * @param absence the Absence entity to convert
     * @return the resulting AbsenceDto
     */
    public static AbsenceDto fromEntity(Absence absence) {
        return AbsenceDto.builder()
                .id(absence.getId())
                .start(absence.getStart())
                .end(absence.getEnd())
                .userId(absence.getUser().getId())
                .absenceTypeId(absence.getAbsenceType().getId())
                .status(absence.getStatus())
                .submission(absence.getSubmission())
                .updated(absence.getUpdated())
                .workingDays(absence.getWorkingDays())
                .employeeId(absence.getUser().getEmployeeId())
                .build();
    }

    /**
     * Converts an AbsenceDto to an Absence entity.
     *
     * @param user        the User entity associated with the absence
     * @param absenceType the AbsenceType entity associated with the absence
     * @param status      the Status entity associated with the absence
     * @return the resulting Absence entity
     */
    public Absence toEntity(User user, AbsenceType absenceType, Status status) {
        return Absence.builder()
                .id(this.id)
                .start(this.start)
                .end(this.end)
                .user(user)
                .absenceType(absenceType)
                .status(status)
                .submission(this.submission)
                .updated(this.updated)
                .workingDays(this.workingDays)
                .build();
    }
}

