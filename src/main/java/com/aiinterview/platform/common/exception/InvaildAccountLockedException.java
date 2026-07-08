package com.hoc.backend.SpringBoot.exception;

import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public class InvaildAccountLockedException extends RuntimeException {


    private LocalDateTime lockedUntil;

    public InvaildAccountLockedException( String message,LocalDateTime lockedUntil) {

        super(message);

        this.lockedUntil = lockedUntil;

    }

    public LocalDateTime getlockUntil () {
        return lockedUntil;
    }

}
