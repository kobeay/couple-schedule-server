package com.kobeay.couple_schedule.server.couple.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class CoupleInvitationResponse {
    private final Long invitationId;
    private final String senderNickname;
    private final Instant createdAt;

    public CoupleInvitationResponse(Long invitationId, String senderNickname, Instant createdAt) {
        this.invitationId = invitationId;
        this.senderNickname = senderNickname;
        this.createdAt = createdAt;
    }
}
