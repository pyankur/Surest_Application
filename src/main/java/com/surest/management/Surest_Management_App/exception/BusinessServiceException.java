package com.surest.management.Surest_Management_App.exception;

import org.springframework.http.HttpStatus;

public class BusinessServiceException extends RuntimeException {

    private final HttpStatus status;

    public BusinessServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}