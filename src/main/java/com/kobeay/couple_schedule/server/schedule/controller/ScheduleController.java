package com.kobeay.couple_schedule.server.schedule.controller;

import com.kobeay.couple_schedule.server.schedule.dto.ScheduleCreateRequest;
import com.kobeay.couple_schedule.server.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public void createSchedule(Authentication authentication, @Valid @RequestBody ScheduleCreateRequest request) {
        Long userId = (Long) authentication.getPrincipal();

        scheduleService.createSchedule(userId, request);
    }
}
