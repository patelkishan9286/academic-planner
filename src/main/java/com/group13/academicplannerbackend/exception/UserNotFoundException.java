package com.group13.academicplannerbackend.exception;

public class UserNotFoundException extends RuntimeException {
    String email;

    public UserNotFoundException(String email) {
        this.email = email;
    }

    @Override
    public String getMessage() {
        return email;
    }
}
