package com.surest.management.Surest_Management_App.exception;

import java.time.OffsetDateTime;

public class ApiError {
    public OffsetDateTime timestamp = OffsetDateTime.now();
    public int status;
    public String message;
}
