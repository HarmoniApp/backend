package org.harmoniapp.controllers.schedule;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.schedule.ScheduleRequestDto;
import org.harmoniapp.contracts.schedule.UserScheduleDto;
import org.harmoniapp.services.schedule.ScheduleServiceImpl;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user schedules.
 * Provides endpoints to retrieve weekly schedules (shifts and absences) for a specific user.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class UserScheduleController {
    private final ScheduleServiceImpl userScheduleService;

    /**
     * Retrieves the weekly schedule for a specific user within a given date range.
     *
     * @param userId             the ID of the user for whom the schedule is being retrieved
     * @param scheduleRequestDto the request DTO containing the start date, end date, and published status
     * @return UserScheduleDto containing the user's details along with their shifts and absences for the specified week
     */
    @GetMapping("/user/{userId}/week")
    public UserScheduleDto getUserWeeklySchedule(@PathVariable Long userId,
                                                 @ModelAttribute ScheduleRequestDto scheduleRequestDto) {
        return userScheduleService.getUserWeeklySchedule(userId, scheduleRequestDto);
    }
}
