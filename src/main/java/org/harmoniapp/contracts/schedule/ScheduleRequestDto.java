package org.harmoniapp.contracts.schedule;

import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

public record ScheduleRequestDto(
        @RequestParam("startDate") LocalDateTime startDate,
        @RequestParam("endDate") LocalDateTime endDate,
        @RequestParam(value = "published", required = false) Boolean published
) {

    public Boolean published() {
        return published != null && published;
    }
}
