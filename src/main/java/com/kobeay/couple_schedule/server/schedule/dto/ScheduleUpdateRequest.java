package com.kobeay.couple_schedule.server.schedule.dto;

import com.kobeay.couple_schedule.server.schedule.entity.ScheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ScheduleUpdateRequest {
    @NotBlank(message = "일정 제목을 입력해 주세요.")
    @Size(max = 30, message = "일정 제목은 최대 30자까지 입력할 수 있습니다.")
    private String title;

    @NotNull(message = "날짜를 선택해 주세요.")
    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Size(max = 300, message = "메모는 최대 300자까지 입력할 수 있습니다.")
    private String memo;

    @NotNull(message = "일정 종류를 선택해 주세요.")
    private ScheduleType type;
}