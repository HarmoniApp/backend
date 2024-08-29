package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object (DTO) for representing a user's weekly schedule with user details.
 *
 * @param userId       the unique identifier of the user
 * @param firstname    the first name of the user
 * @param surname      the surname of the user
 * @param weekSchedule the weekly schedule containing shifts and absences
 */
public record UserScheduleDto(@JsonProperty("user_id") Long userId, String firstname, String surname, WeekScheduleDto weekSchedule) {
}
