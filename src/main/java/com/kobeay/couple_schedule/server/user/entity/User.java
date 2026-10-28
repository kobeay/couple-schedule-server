package com.kobeay.couple_schedule.server.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    public User(
            String email,
            String passwordHash,
            String nickname,
            LoginType loginType,
            String inviteCode
    ) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.loginType = loginType;
        this.inviteCode = inviteCode;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    @Column(name = "invite_code", unique = true)
    private String inviteCode;
}