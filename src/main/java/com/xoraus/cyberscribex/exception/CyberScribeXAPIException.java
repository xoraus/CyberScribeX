package com.xoraus.cyberscribex.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public class CyberScribeXAPIException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final HttpStatus httpStatus;
    private final String message;

    public CyberScribeXAPIException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public CyberScribeXAPIException(String parentMessage, HttpStatus httpStatus, String message) {
        super(parentMessage);
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
