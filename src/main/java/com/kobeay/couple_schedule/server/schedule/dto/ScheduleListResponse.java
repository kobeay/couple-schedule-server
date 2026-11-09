package com.kobeay.couple_schedule.server.schedule.dto;

import com.kobeay.couple_schedule.server.schedule.entity.Schedule;
import com.kobeay.couple_schedule.server.schedule.entity.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ScheduleListResponse {
    private Long id;
    private Long userId;
    private String title;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private ScheduleType type;

    public static ScheduleListResponse from(Schedule schedule) {
        return new ScheduleListResponse(
                schedule.getId(),
                schedule.getUser().getId(),
                schedule.getTitle(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getType()
        );
    }
}
