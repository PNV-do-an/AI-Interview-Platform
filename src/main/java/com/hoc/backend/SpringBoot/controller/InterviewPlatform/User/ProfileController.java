package com.hoc.backend.SpringBoot.controller.InterviewPlatform.User;

import com.hoc.backend.SpringBoot.dto.ProfileResponse;
import com.hoc.backend.SpringBoot.repository.UserRepository;
import com.hoc.backend.SpringBoot.service.InterviewPlatform.User.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class ProfileController {

    private ProfileService profileService;

    public ProfileController(ProfileService profileService) {this.profileService = profileService;}

    @GetMapping("/profile")
    public ProfileResponse profile (Authentication authentication) {
        return profileService.getInfor(authentication.getName());

    }
}
