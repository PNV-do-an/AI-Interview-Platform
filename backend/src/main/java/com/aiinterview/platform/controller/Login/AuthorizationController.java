package com.aiinterview

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthorizationController {
    @GetMapping("/test")
    public String test(Authentication auth) {
        if (auth == null) {
            return "Unauthorized";
        }
        return "Hello " + auth.getName();
    }
}
