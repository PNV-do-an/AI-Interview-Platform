package com.hoc.backend.SpringBoot.dto;

import lombok.Getter;
import lombok.Setter;

public class RegisterRequest {
    @Getter
    @Setter
    private String account;

    @Getter
    @Setter
    private String password;


}
