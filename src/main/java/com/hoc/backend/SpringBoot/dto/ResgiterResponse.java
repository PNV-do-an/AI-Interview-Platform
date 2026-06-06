package com.hoc.backend.SpringBoot.dto;

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
