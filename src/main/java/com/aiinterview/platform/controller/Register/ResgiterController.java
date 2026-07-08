package com.aiinterview.platform.controller.Register;

import com.aiinterview.platform.model.dto.request.RegisterRequest;
import com.aiinterview.platform.model.dto.response.AuthResponse;
import com.aiinterview.platform.model.dto.response.ResgiterResponse;
import com.aiinterview.platform.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class ResgiterController {
    private final AuthService authService;

    public ResgiterController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResgiterResponse resgiterAccount(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        AuthResponse response = authService.register(request);
        return new ResgiterResponse(response.getUser().getFullName() + " registered successfully");
    }
}
