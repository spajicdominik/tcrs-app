package com.tcrs_app.tcrs_app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TournamentException extends RuntimeException {

    private final HttpStatus status;

    public TournamentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
