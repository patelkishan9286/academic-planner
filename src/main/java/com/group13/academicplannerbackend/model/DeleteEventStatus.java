package com.group13.academicplannerbackend.model;

import org.springframework.http.HttpStatus;

public enum DeleteEventStatus {
    SUCCESS(HttpStatus.OK),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    NOT_AUTHORIZED(HttpStatus.FORBIDDEN);

    private final HttpStatus httpStatus;

    DeleteEventStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
