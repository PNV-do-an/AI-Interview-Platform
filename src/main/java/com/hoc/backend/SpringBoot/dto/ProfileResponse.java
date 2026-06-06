package com.hoc.backend.SpringBoot.dto;

import lombok.Getter;
import lombok.Setter;

public class ProfileResponse {

    @Setter
    @Getter
    private String account;

    @Setter
    @Getter
    private int counterFail;

    @Setter
    @Getter
    private String role;

    public ProfileResponse (String account, int counterFail, String role) {
        this.account = account;
        this.counterFail = counterFail;
        this.role =role;
    }

}
