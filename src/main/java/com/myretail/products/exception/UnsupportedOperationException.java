package com.myretail.products.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class UnsupportedOperationException extends RuntimeException {

    public UnsupportedOperationException(String message) {
        super(message);
    }
}
