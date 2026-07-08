package com.aiinterview.platform.controller.Login;

import com.aiinterview.platform.model.dto.request.RefreshTokenRequest;
import com.aiinterview.platform.model.dto.response.AuthResponse;
import com.aiinterview.platform.model.dto.response.LoginResponse;
import com.aiinterview.platform.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class RefreshTokenController {
    private final AuthService authService;

    public RefreshTokenController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/refresh")
    public LoginResponse refreshAccessToken(@RequestBody RefreshTokenRequest refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken.getRefreshToken());
        return new LoginResponse(
                response.getAccessToken(),
                response.getRefreshToken(),
                response.getUser().getRole()
        );
    }
}
