package com.aiinterview.platform.common.exception;

import com.aiinterview.platform.model.entity.ErrorModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> handleInvalidPassword(InvalidPasswordException ex, HttpServletRequest request) {
        ErrorModel error =  new ErrorModel(401,ex.getMessage(), request.getRequestURL().toString(), LocalDateTime.now(), // base information about bug
                new HashMap<>()); // if any information about bug

        return ResponseEntity .status(401) .body(error);
    }

    @ExceptionHandler(InvalidAccountException.class)
    public ResponseEntity<?> handleInvalidAccount(InvalidAccountException ex, HttpServletRequest request) {
        ErrorModel error = new ErrorModel(401,ex.getMessage(), request.getRequestURL().toString(), LocalDateTime.now(),
                new HashMap<>());
        return ResponseEntity.status(401).body(error);
    }

    @ExceptionHandler(InvalidAccountLockedException.class)
    public ResponseEntity<?> handleInvalidAccountLocked(InvalidAccountLockedException ex,HttpServletRequest request) {
        ErrorModel error = new ErrorModel(423,ex.getMessage(), request.getRequestURL().toString(), LocalDateTime.now(),
                new HashMap<>(
                        Map.of(
                                "lockedUntil", ex.getLockUntil()
                        )
                ));
        return ResponseEntity.status(423).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleOther(RuntimeException ex, HttpServletRequest request) {
        ErrorModel error = new ErrorModel(500,"Internal serve error", request.getRequestURL().toString(), LocalDateTime.now(),
                new HashMap<>(
                ));
        return ResponseEntity.status(500).body(error);
    }
}