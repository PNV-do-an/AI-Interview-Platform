package com.hoc.backend.SpringBoot.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorModel { // a model for response error

    @Getter
    @Setter
    private int status;

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private LocalDateTime timestamp;

    @Getter
    @Setter
    private Map<String,Object> detail; // this attribute contain all details from different bugs

    public ErrorModel (int status,String message,String path, LocalDateTime timestamp,Map<String,Object> detail) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.detail = detail;
    }


}
