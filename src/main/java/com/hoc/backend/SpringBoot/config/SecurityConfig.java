package com.hoc.backend.SpringBoot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // tắt CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/service/login").permitAll() // cho phép login
                        .anyRequest().permitAll() // tạm thời mở hết
                );

        return http.build();
    }
}
