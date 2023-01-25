package com.group13.academicplannerbackend.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
