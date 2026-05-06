package com.hoc.backend.SpringBoot.controller.InterviewPlatform;

import com.hoc.backend.SpringBoot.service.InterviewPlatform.LoginService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Array;
import java.util.ArrayList;

@RestController
@RequestMapping("api/auth")
public class LoginController {



}

//    -- demo
//    private LoginService loginService;
//
//    public LoginController(LoginService loginService) {
//        this.loginService = loginService;
//    };
//
//    @GetMapping("service/login")
//    public String login(@RequestParam String account, @RequestParam String passWord) {
//        return loginService.verify(account,passWord);
//    }
