package com.kobeay.couple_schedule.server.couple.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CoupleMetDateUpdateRequest {
    @NotNull(message = "만난 날짜를 선택해 주세요.")
    private LocalDate metDate;
}
