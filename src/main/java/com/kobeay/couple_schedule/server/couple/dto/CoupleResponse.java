package com.kobeay.couple_schedule.server.couple.dto;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
public class CoupleResponse {
    private boolean connected;
    private String partnerNickname;
    private Instant connectedAt;
    private LocalDate metDate;
    private String partnerProfileImageUrl;

    public CoupleResponse(boolean connected, String partnerNickname, Instant connectedAt, LocalDate metDate, String partnerProfileImageUrl) {
        this.connected = connected;
        this.partnerNickname = partnerNickname;
        this.connectedAt = connectedAt;
        this.metDate = metDate;
        this.partnerProfileImageUrl = partnerProfileImageUrl;
    }
}
