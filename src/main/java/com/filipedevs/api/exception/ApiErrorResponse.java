package com.filipedevs.api.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
public class ApiErrorResponse {

    private HttpStatus httpStatus;
    private String message;
    private String path;
    private String api;
    private ZonedDateTime timestamp;

    public ApiErrorResponse(HttpStatus httpStatus, String message, String path, String api, ZonedDateTime timestamp) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.path = path;
        this.api = api;
        this.timestamp = timestamp;
    }
}
