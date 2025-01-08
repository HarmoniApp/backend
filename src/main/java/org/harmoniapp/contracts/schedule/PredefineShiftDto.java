package org.harmoniapp.contracts.schedule;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.harmoniapp.entities.schedule.PredefineShift;

import java.time.LocalTime;

/**
 * Data Transfer Object for PredefineShift.
 *
 * @param id    the unique identifier of the predefined shift
 * @param name  the name of the predefined shift
 * @param start the start time of the predefined shift
 * @param end   the end time of the predefined shift
 */
@Builder
public record PredefineShiftDto(
        long id,

        @NotEmpty(message = "Nazwa zmiany nie może być pusta")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$", message = "Nazwa musi zawierać tylko litery, spacje, myślniki i apostrofy")
        String name,

        @NotNull(message = "Czas rozpoczęcia nie może być pusty")
        LocalTime start,

        @NotNull(message = "Czas zakończenia nie może być pusty")
        LocalTime end) {

    /**
     * Converts a PredefineShift entity to a PredefineShiftDto.
     *
     * @param predefineShift the PredefineShift entity to convert
     * @return the resulting PredefineShiftDto
     */
    public static PredefineShiftDto fromEntity(PredefineShift predefineShift) {
        return PredefineShiftDto.builder()
                .id(predefineShift.getId())
                .name(predefineShift.getName())
                .start(predefineShift.getStart())
                .end(predefineShift.getEnd())
                .build();
    }

    /**
     * Converts a PredefineShiftDto to a PredefineShift entity.
     *
     * @return the resulting PredefineShift entity
     */
    public PredefineShift toEntity() {
        return PredefineShift.builder()
                .id(this.id)
                .name(this.name)
                .start(this.start)
                .end(this.end)
                .build();
    }
}
