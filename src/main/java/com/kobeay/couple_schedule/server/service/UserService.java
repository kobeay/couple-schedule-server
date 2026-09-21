package com.kobeay.couple_schedule.server.service;

import com.kobeay.couple_schedule.server.dto.SignUpRequest;
import com.kobeay.couple_schedule.server.entity.LoginType;
import com.kobeay.couple_schedule.server.entity.User;
import com.kobeay.couple_schedule.server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                LoginType.LOCAL
        );

        userRepository.save(user);
    }
}
