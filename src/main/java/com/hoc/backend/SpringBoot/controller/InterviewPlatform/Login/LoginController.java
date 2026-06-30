package com.hoc.backend.SpringBoot.controller.InterviewPlatform.Login;

import com.hoc.backend.SpringBoot.dto.LoginRequest;
import com.hoc.backend.SpringBoot.dto.LoginResponse;
import com.hoc.backend.SpringBoot.service.InterviewPlatform.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return loginService.verify(
                request.getEmail(),
                request.getPassword(),
                httpRequest
        );
    }
}
