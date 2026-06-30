package com.hoc.backend.SpringBoot.dto;

import lombok.Getter;
import lombok.Setter;

public class RegisterRequest {
    @Getter @Setter private String email;
    @Getter @Setter private String password;
    @Getter @Setter private String fullName;
    @Getter @Setter private String phone;
}
