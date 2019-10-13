package com.salil.shawrtn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Url not found for the given key")
public class UrlNotFoundException extends Exception {

    public UrlNotFoundException(String message) {
        super(message);
    }
}
