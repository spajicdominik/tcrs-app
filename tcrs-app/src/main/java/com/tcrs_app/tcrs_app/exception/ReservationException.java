package com.tcrs_app.tcrs_app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReservationException extends RuntimeException {

    private final HttpStatus status;

    public ReservationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
