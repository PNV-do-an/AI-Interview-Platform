package com.aiinterview.platform.model.dto.response;

import lombok.Getter;
import lombok.Setter;

public class ProfileResponse {

    @Setter @Getter private String email;
    @Setter @Getter private String fullName;
    @Setter @Getter private String phone;
    @Setter @Getter private int counterFail;
    @Setter @Getter private String role;

    public ProfileResponse(String email, String fullName, String phone, int counterFail, String role) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.counterFail = counterFail;
        this.role = role;
    }
}
