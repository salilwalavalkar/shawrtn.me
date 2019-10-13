package com.salil.shawrtn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Url is invalid")
public class UrlInvalidException extends Exception {

    public UrlInvalidException(String message) {
        super(message);
    }
}
