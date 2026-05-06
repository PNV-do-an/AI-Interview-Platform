    package com.hoc.backend.SpringBoot.controller.InterviewPlatform;

    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.GetMapping;

    @Controller
    public class ViewController {
        @GetMapping("/")
        public static String returnView() {
            return "indexLogin.html";
        }
    }
