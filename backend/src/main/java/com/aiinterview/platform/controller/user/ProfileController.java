package com.aiinterview.platform.controller.user;

import com.aiinterview.platform.model.dto.response.ProfileResponse;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ProfileResponse profile(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return new ProfileResponse(
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getLoginAttempts(),
                user.getRole().name()
        );
    }
}
