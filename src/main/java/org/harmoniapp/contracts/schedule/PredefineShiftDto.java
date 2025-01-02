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

        @NotEmpty(message = "Shift name cannot be empty")
        @Pattern(regexp = "^[A-Za-z Ā-ɏØ-öø-ÿ'\\-\\s]+$", message = "Shift name must contain only letters, digits, dashes, and spaces")
        String name,

        @NotNull(message = "Start time cannot be null")
        LocalTime start,

        @NotNull(message = "End time cannot be null")
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
