package com.hoc.backend.SpringBoot.service.Practice;

import org.springframework.stereotype.Service;

@Service
public class Hello2Service {
    public String sayName(String name) {
        return "Hello " + name ;
    }
}
