package com.aiinterview.platform.model.dto.response;

import lombok.Getter;
import lombok.Setter;

public class RegisterResponse {
    @Getter
    @Setter
    private String message;

    public RegisterResponse (String message) {
        this.message = message;
    }

}
