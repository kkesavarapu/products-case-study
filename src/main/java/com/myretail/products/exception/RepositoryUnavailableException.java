package com.myretail.products.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RepositoryUnavailableException extends RuntimeException {

    public RepositoryUnavailableException(String message) {
        super(message);
    }
}
