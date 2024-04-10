package com.xoraus.cyberscribex.exception;

import org.springframework.http.HttpStatus;

public class CyberScribeXAPIException extends RuntimeException{
    private HttpStatus httpStatus;
    private String message;

    public CyberScribeXAPIException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public CyberScribeXAPIException(String parentMessage, HttpStatus httpStatus, String message) {
        super(parentMessage);
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
