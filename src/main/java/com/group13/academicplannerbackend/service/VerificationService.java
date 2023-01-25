package com.group13.academicplannerbackend.service;

public interface VerificationService {
    void verify(String code, String email);
    void sendVerificationEmail(String email);
}
