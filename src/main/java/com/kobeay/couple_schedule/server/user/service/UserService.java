package com.kobeay.couple_schedule.server.user.service;

import com.kobeay.couple_schedule.server.user.dto.LoginRequest;
import com.kobeay.couple_schedule.server.user.dto.LoginResponse;
import com.kobeay.couple_schedule.server.user.dto.SignUpRequest;
import com.kobeay.couple_schedule.server.user.entity.LoginType;
import com.kobeay.couple_schedule.server.user.dto.RefreshRequest;
import com.kobeay.couple_schedule.server.user.entity.RefreshToken;
import com.kobeay.couple_schedule.server.user.entity.User;
import com.kobeay.couple_schedule.server.global.jwt.JwtTokenProvider;
import com.kobeay.couple_schedule.server.global.jwt.RefreshTokenHasher;
import com.kobeay.couple_schedule.server.user.repository.RefreshTokenRepository;
import com.kobeay.couple_schedule.server.user.repository.UserRepository;
import com.kobeay.couple_schedule.server.user.util.InviteCodeGenerator;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenHasher refreshTokenHasher;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenHasher refreshTokenHasher
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenHasher = refreshTokenHasher;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void signup(SignUpRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPasswordConfirm());

        String inviteCode;

        do {
            inviteCode = InviteCodeGenerator.generate();
        } while (userRepository.existsByInviteCode(inviteCode));

        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                LoginType.LOCAL,
                inviteCode
        );

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 원본 비밀번호와 암호화된 비밀번호가 일치하는가
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        String tokenHash = refreshTokenHasher.hash(refreshToken);

        // Refresh Token 만료 시각 추출
        Instant expiresAt = jwtTokenProvider.getExpiration(refreshToken);

        // Refresh Token DB 저장
        RefreshToken tokenEntity = new RefreshToken(user, tokenHash, expiresAt);
        refreshTokenRepository.save(tokenEntity);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse refresh(RefreshRequest request) {
        // 1. Refresh Token 추출
        String refreshToken = request.getRefreshToken();

        // 2. JWT 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
        }

        // 3. Refresh Token 타입 확인
        if (!"refresh".equals(jwtTokenProvider.getTokenType(refreshToken))) {
            throw new BadCredentialsException("Refresh Token이 아닙니다.");
        }

        // 4. Refresh Token 해시 생성
        String tokenHash = refreshTokenHasher.hash(refreshToken);

        // 5. DB에서 Refresh Token 조회
        RefreshToken tokenEntity = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("등록되지 않은 Refresh Token입니다."));

        // 6. DB에 저장된 만료 시각 확인
        if (!tokenEntity.getExpiresAt().isAfter(Instant.now())) {
            throw new BadCredentialsException("만료된 Refresh Token입니다.");
        }

        // 7. JWT의 사용자 ID와 DB의 사용자 ID 비교
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        if (!tokenEntity.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("Refresh Token 사용자 정보가 일치하지 않습니다.");
        }

        // 8. 새로운 AccessToken, Refresh Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        // 9. 새로운 Refresh Token 해시 및 만료 시각 추출
        String newTokenHash = refreshTokenHasher.hash(newRefreshToken);
        Instant newExpiresAt = jwtTokenProvider.getExpiration(newRefreshToken);

        // 10. 기존 Refresh Token 정보 교체
        tokenEntity.rotate(newTokenHash, newExpiresAt);

        // 11. 새로운 토큰 반환
        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(RefreshRequest request) {
        // 1. Refresh Token 추출
        String refreshToken = request.getRefreshToken();

        // 2. JWT 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
        }

        // 3. Refresh Token 타입 확인
        if (!"refresh".equals(jwtTokenProvider.getTokenType(refreshToken))) {
            throw new BadCredentialsException("Refresh Token이 아닙니다.");
        }

        // 4. Refresh Token 해시 생성
        String tokenHash = refreshTokenHasher.hash(refreshToken);

        // 5. DB에서 Refresh Token 조회
        RefreshToken tokenEntity = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("등록되지 않은 Refresh Token입니다."));

        // 6. JWT 사용자 ID와 DB 사용자 ID 비교
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        if (!tokenEntity.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("Refresh Token 사용자 정보가 일치하지 않습니다.");
        }

        // 7. DB에서 Refresh Token 삭제
        refreshTokenRepository.delete(tokenEntity);
    }
}
