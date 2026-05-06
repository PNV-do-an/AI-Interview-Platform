package com.hoc.backend.SpringBoot.controller.Practice;
// return file
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReturnController {
    @GetMapping("/html")
    public static String index() {
        return "index.html";
    }
}
