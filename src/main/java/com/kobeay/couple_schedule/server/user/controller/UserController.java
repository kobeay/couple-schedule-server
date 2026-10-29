package com.kobeay.couple_schedule.server.user.controller;

import com.kobeay.couple_schedule.server.user.dto.*;
import com.kobeay.couple_schedule.server.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public void signup(@Valid @RequestBody SignUpRequest request) {
        userService.signup(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        return userService.getMyInfo(userId);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return userService.refresh(request);
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody RefreshRequest request) {
        userService.logout(request);
    }
}
