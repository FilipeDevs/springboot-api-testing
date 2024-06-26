package com.filipedevs.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CustomerEmailUnavailableException extends RuntimeException{
    public CustomerEmailUnavailableException(String message) {
        super(message);
    }
}
