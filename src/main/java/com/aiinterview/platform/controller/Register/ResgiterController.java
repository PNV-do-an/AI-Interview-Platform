package com.hoc.backend.SpringBoot.controller.InterviewPlatform.Register;

import com.hoc.backend.SpringBoot.dto.RegisterRequest;
import com.hoc.backend.SpringBoot.dto.ResgiterResponse;
import com.hoc.backend.SpringBoot.service.InterviewPlatform.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class ResgiterController {
    private RegisterService registerService;

    public ResgiterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register")
    public ResgiterResponse resgiterAccount(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        return registerService.resgiter(
                request.getEmail(),
                request.getPassword(),
                request.getFullName(),
                request.getPhone(),
                httpRequest
        );
    }
}
