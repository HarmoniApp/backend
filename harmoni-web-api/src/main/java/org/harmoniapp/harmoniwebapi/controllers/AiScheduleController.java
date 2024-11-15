package org.harmoniapp.harmoniwebapi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.AiSchedule.AiSchedulerResponse;
import org.harmoniapp.harmoniwebapi.contracts.AiSchedule.ScheduleRequirement;
import org.harmoniapp.harmoniwebapi.services.AiScheduleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/aiSchedule")
public class AiScheduleController {
    private final AiScheduleService service;


    @PostMapping("/generate")
    public AiSchedulerResponse generateSchedule(@Valid @RequestBody List<ScheduleRequirement> requirements) {
        return service.generateSchedule(requirements);
    }
}
