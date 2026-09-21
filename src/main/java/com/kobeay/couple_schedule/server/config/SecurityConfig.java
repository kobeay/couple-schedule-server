package com.kobeay.couple_schedule.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // HTTP 요청이 들어왔을 때 어떤 요청은 허용하고
    // 어떤 요청은 인증이 필요한지 정하는 보안 규칙
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception {
        // CSRF 보안 비활성화
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/signup").permitAll()
                        .anyRequest().authenticated()
                );

        // 지금까지 설정한 보안 규칙을 가지고 최종 SecurityFilterChain 만들어 반환
        return http.build();
    }
}
