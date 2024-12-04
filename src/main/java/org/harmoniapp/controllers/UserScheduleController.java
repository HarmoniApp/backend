package org.harmoniapp.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.UserScheduleDto;
import org.harmoniapp.services.UserScheduleService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller for managing user schedules.
 * Provides endpoints to retrieve weekly schedules (shifts and absences) for a specific user.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class UserScheduleController {
    private final UserScheduleService userScheduleService;

    /**
     * Retrieves the weekly schedule for a specific user within a given date range.
     *
     * @param userId    the ID of the user for whom the schedule is being retrieved
     * @param startDate the start date of the week (as LocalDateTime) to filter shifts and absences
     * @param endDate   the end date of the week (as LocalDateTime) to filter shifts and absences
     * @param published an optional parameter; if true, only published shifts are returned, otherwise all shifts
     * @return UserScheduleDto containing the user's details along with their shifts and absences for the specified week
     */
    @GetMapping("/user/{userId}/week")
    public UserScheduleDto getUserWeeklySchedule(
            @PathVariable Long userId,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate,
            @RequestParam(value = "published", required = false) boolean published) {
        return userScheduleService.getUserWeeklySchedule(userId, startDate, endDate, published);
    }
}
