package com.aiinterview.platform.controller.Login;

package com.aiinterview.platform.controller.Login;

import com.aiinterview.platform.model.dto.request.LoginRequest;
import com.aiinterview.platform.model.dto.response.AuthResponse;
import com.aiinterview.platform.model.dto.response.LoginResponse;
import com.aiinterview.platform.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        AuthResponse response = authService.login(request);
        return new LoginResponse(
                response.getAccessToken(),
                response.getRefreshToken(),
                response.getUser().getRole()
        );
    }
}
