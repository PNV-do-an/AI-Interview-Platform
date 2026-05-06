package com.hoc.backend.SpringBoot.controller.Practice;

import com.hoc.backend.SpringBoot.service.Practice.Hello2Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello2Controller {

    private Hello2Service hello2Service;

    public Hello2Controller (Hello2Service hello2Service) {

        this.hello2Service = hello2Service;
    }

    @GetMapping("api/sayname")
    public String getSayName(@RequestParam("name") String name ) { // RequestParam truyển dữ liệu vào
        return hello2Service.sayName(name);
    }
}
