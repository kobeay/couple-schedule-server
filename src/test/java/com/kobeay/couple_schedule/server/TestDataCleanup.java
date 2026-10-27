package com.kobeay.couple_schedule.server;

import com.kobeay.couple_schedule.server.user.repository.RefreshTokenRepository;
import com.kobeay.couple_schedule.server.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestDataCleanup {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public TestDataCleanup(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void cleanup(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            // 1. 회원의 Refresh Token 삭제
            refreshTokenRepository.deleteByUserId(user.getId());

            // 2. 테스트 회원 삭제
            userRepository.delete(user);
        });
    }
}