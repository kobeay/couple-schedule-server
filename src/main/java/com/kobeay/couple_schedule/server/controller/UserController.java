package com.kobeay.couple_schedule.server.controller;

import com.kobeay.couple_schedule.server.dto.LoginRequest;
import com.kobeay.couple_schedule.server.dto.LoginResponse;
import com.kobeay.couple_schedule.server.dto.SignUpRequest;
import com.kobeay.couple_schedule.server.service.UserService;
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
        String token = userService.login(request);

        return new LoginResponse(token);
    }

    @GetMapping("/me")
    public Long me(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
