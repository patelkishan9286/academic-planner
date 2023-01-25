package com.group13.academicplannerbackend.model;

import org.springframework.http.HttpStatus;

public enum UpdateEventStatus {
    SUCCESS(HttpStatus.OK),
    NOT_RESCHEDULABLE(HttpStatus.BAD_REQUEST),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    NOT_AUTHORIZED(HttpStatus.FORBIDDEN);

    private final HttpStatus httpStatus;

    UpdateEventStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
