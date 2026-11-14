package com.kobeay.couple_schedule.server.schedule.controller;

import com.kobeay.couple_schedule.server.schedule.dto.ScheduleCreateRequest;
import com.kobeay.couple_schedule.server.schedule.dto.ScheduleListResponse;
import com.kobeay.couple_schedule.server.schedule.dto.ScheduleUpdateRequest;
import com.kobeay.couple_schedule.server.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public void createSchedule(
            Authentication authentication,
            @Valid @RequestBody ScheduleCreateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        scheduleService.createSchedule(userId, request);
    }

    @GetMapping
    public List<ScheduleListResponse> getMonthlySchedules(
            Authentication authentication,
            @RequestParam int year,
            @RequestParam int month
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return scheduleService.getMonthlySchedules(userId, year, month);
    }

    @PutMapping("/{scheduleId}")
    public void updateSchedule(
            Authentication authentication,
            @Valid @RequestBody ScheduleUpdateRequest request,
            @PathVariable Long scheduleId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        scheduleService.updateSchedule(request, userId, scheduleId);
    }

    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(
            Authentication authentication,
            @PathVariable Long scheduleId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        scheduleService.deleteSchedule(userId, scheduleId);
    }
}
