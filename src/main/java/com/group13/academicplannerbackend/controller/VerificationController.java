package com.group13.academicplannerbackend.controller;

import com.group13.academicplannerbackend.exception.VerificationException;
import com.group13.academicplannerbackend.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam("code") String code, @RequestParam("email") String email) {
        String responseBody;
        String Head="<!DOCTYPE html><html><head><title>Email Verification</title>";
        
        try {
            verificationService.verify(code, email);
            responseBody = Head+"</head><body><h1>Email verification successful</h1><p>Please <a href='/auth/login'>log in</a> to continue.</p></body></html>";
            return ResponseEntity.ok(responseBody);
        } catch (VerificationException e) {
            boolean alreadyVerified = "User is already verified".equals(e.getMessage());
            String extraMessage = alreadyVerified ? "<p>Please <a href='/auth/login'>log in</a> to continue.</p>" : "<p>Please <a href='/resend-verification?email=" + email + "'>resend the verification email</a> and try again.</p>";
            responseBody = Head+"</head><body><h1>Email verification failed</h1><p>" + e.getMessage() + ".</p>" + extraMessage + "</body></html>";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    @GetMapping("/resend-verification")
    public void resendVerificationEmail(@RequestParam("email") String email) {
        verificationService.sendVerificationEmail(email);
    }
}
