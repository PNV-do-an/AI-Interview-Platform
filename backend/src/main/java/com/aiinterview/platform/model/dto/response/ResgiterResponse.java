package com.aiinterview.platform.model.dto.response;

import lombok.Getter;
import lombok.Setter;

public class ResgiterResponse {
    @Getter
    @Setter
    private String message;

    public ResgiterResponse (String message) {
        this.message = message;
    }

}
