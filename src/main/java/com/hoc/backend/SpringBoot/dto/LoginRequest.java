package com.hoc.backend.SpringBoot.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;

// lombok is a popular Java library used to reduce "boilerplate" code. like setter,getter,constructor,... ( boilerplate : is a code, text, document can be reused )
public class LoginRequest {

    @Getter
    @Setter
    private String account;
    @Getter
    @Setter
    private String password;



}
