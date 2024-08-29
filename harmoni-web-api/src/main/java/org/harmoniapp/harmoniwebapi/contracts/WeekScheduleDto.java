package org.harmoniapp.harmoniwebapi.contracts;

import java.util.List;

/**
 * Data Transfer Object (DTO) for representing a user's weekly schedule with shifts and absences
 *
 * @param shifts   a list of ShiftDto representing the shifts scheduled for the user in the specified week
 * @param absences a list of AbsenceDto representing the absences (approved leaves) of the user in the specified week
 */
public record WeekScheduleDto(List<ShiftDto> shifts, List<AbsenceDto> absences) {
}
