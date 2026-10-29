package com.kobeay.couple_schedule.server.user.dto;

import lombok.Getter;

@Getter
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String inviteCode;

    public UserResponse(Long id, String email, String nickname, String inviteCode) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.inviteCode = inviteCode;
    }
}
