package com.hoc.backend.SpringBoot.exception;

public class InvalidPassWordException extends RuntimeException{
    public InvalidPassWordException(String message) {
        super(message);
    }
}
