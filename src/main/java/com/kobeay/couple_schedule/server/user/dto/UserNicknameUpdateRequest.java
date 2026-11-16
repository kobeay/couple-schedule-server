package com.kobeay.couple_schedule.server.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserNicknameUpdateRequest {
    @NotBlank(message = "닉네임을 입력해 주세요.")
    @Size(min = 2, max = 12, message = "닉네임은 2 ~ 12자로 해주세요.")
    private String nickname;
}
