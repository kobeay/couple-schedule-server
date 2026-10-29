package com.kobeay.couple_schedule.server.couple.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class CoupleResponse {
    private boolean connected;
    private String partnerNickname;
    private Instant connectedAt;

    public CoupleResponse(boolean connected, String partnerNickname, Instant connectedAt) {
        this.connected = connected;
        this.partnerNickname = partnerNickname;
        this.connectedAt = connectedAt;
    }
}
