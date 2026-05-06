package com.hoc.backend.SpringBoot.controller.Practice;

import com.hoc.backend.SpringBoot.service.Practice.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private HelloService helloService;

    public HelloController (HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("api/hello_world")
    public String getHello() {
        return  helloService.sayHelloWorld();
    }
}
