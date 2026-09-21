package com.kobeay.couple_schedule.server.controller;

import com.kobeay.couple_schedule.server.dto.SignUpRequest;
import com.kobeay.couple_schedule.server.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 이 클래스는 HTTP 요청을 받는 Controller
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    // HTTP 요청의 Body에 들어있는 데이터를 이 매개변수에 넣어줘
    public void signup(@Valid @RequestBody SignUpRequest request) {
        userService.signup(request);
    }
}
