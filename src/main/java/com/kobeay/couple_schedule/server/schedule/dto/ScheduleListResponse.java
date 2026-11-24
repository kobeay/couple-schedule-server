package com.kobeay.couple_schedule.server.schedule.dto;

import com.kobeay.couple_schedule.server.schedule.entity.RepeatType;
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
    private LocalDate originalDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String memo;
    private ScheduleType type;
    private RepeatType repeatType;
    private LocalDate repeatEndDate;

    public static ScheduleListResponse from(Schedule schedule, LocalDate date) {
        return new ScheduleListResponse(
                schedule.getId(),
                schedule.getUser().getId(),
                schedule.getTitle(),
                date,
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getMemo(),
                schedule.getType(),
                schedule.getRepeatType(),
                schedule.getRepeatEndDate()
        );
    }
}