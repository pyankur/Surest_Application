package com.surest.management.Surest_Management_App.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException ex) {
        var err = new ApiError();
        err.status = HttpStatus.BAD_REQUEST.value();
        err.message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        var err = new ApiError();
        err.status = HttpStatus.BAD_REQUEST.value();
        err.message =
                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAnyEception(Exception ex) {
        var err = new ApiError();
        err.status = HttpStatus.BAD_REQUEST.value();
        err.message =
                ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }
}
