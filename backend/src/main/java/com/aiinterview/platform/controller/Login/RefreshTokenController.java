package com.hoc.backend.SpringBoot.controller.InterviewPlatform.Login;

import com.hoc.backend.SpringBoot.dto.LoginResponse;
import com.hoc.backend.SpringBoot.dto.RefreshTokenRequest;
import com.hoc.backend.SpringBoot.service.InterviewPlatform.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class RefreshTokenController {
    private LoginService loginService;
    public RefreshTokenController(LoginService loginService) {
        this.loginService = loginService;
    }
    @PostMapping("/refresh")
    public LoginResponse refreshAccessToken(@RequestBody RefreshTokenRequest refreshToken) {
        return loginService.refreshAccessToken(refreshToken.getRefreshToken());
    }
}
