package com.aiinterview.platform.common.exception;

import java.time.LocalDateTime;

public class InvalidAccountLockedException extends RuntimeException {

    private LocalDateTime lockedUntil;

    public InvalidAccountLockedException(String message, LocalDateTime lockedUntil) {
        super(message);
        this.lockedUntil = lockedUntil;
    }

    public LocalDateTime getLockUntil() {
        return lockedUntil;
    }
}
