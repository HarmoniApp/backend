package org.harmoniapp.contracts.schedule;

import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for scheduling requests.
 *
 * @param startDate the start date and time of the schedule
 * @param endDate   the end date and time of the schedule
 * @param published the published status of the schedule, optional
 */
public record ScheduleRequestDto(
        @RequestParam("startDate") LocalDateTime startDate,
        @RequestParam("endDate") LocalDateTime endDate,
        @RequestParam(value = "published", required = false) Boolean published) {

    /**
     * Returns the published status of the schedule.
     *
     * @return true if published is not null and true, false otherwise
     */
    public Boolean published() {
        return published != null && published;
    }
}
