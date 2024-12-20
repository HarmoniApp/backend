package org.harmoniapp.controllers.schedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.schedule.aischedule.AiSchedulerResponse;
import org.harmoniapp.contracts.schedule.aischedule.ScheduleRequirement;
import org.harmoniapp.services.schedule.aischedule.AiScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing AI schedule generation.
 * Provides endpoints to generate schedules based on requirements.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/aiSchedule")
public class AiScheduleController {
    private final AiScheduleService service;


    /**
     * Generates a schedule based on the provided requirements.
     *
     * @param requirements the list of schedule requirements
     * @param authentication the authentication information of the user
     * @return a ResponseEntity containing the generated schedule
     */
    @PostMapping("/generate")
    public ResponseEntity<AiSchedulerResponse> generateSchedule(@Valid @RequestBody List<ScheduleRequirement> requirements, Authentication authentication) {
        return service.generateSchedule(requirements, authentication);
    }

    /**
     * Revokes the last generated schedule.
     *
     * @return an AiSchedulerResponse containing the result of the revocation
     */
    @PostMapping("/revoke")
    public AiSchedulerResponse revokeSchedule() {
        return service.revokeSchedule();
    }
}
