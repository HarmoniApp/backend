package org.harmoniapp.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data Transfer Object (DTO) for representing a user's weekly schedule with shifts and absences
 *
 * @param userId           the unique identifier of the user
 * @param shifts           a list of ShiftDto representing the shifts scheduled for the user in the specified week
 * @param absences a list of PartialAbsenceDto representing the approved absences of the user in the specified week
 */
public record UserScheduleDto(@JsonProperty("user_id") Long userId, List<ShiftDto> shifts, List<PartialAbsenceDto> absences) {
}
