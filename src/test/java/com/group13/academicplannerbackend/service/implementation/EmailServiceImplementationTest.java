package com.group13.academicplannerbackend.service.implementation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.group13.academicplannerbackend.service.EmailService;

public class EmailServiceImplementationTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImplementation emailService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendEmailSuccess() {
        String to = "pankti@gmail.com";
        String subject = "Academic Planner Login Verification";
        String body = "Do Verify your email click on this email";

        emailService.sendEmail(to, subject, body);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendEmailWithEmptyToEmailAddress() {
        String to = "";
        String subject = "Academic Planner Login Verification";
        String body = "Do Verify your email click on this email";

        Assertions.assertDoesNotThrow(() -> {
            emailService.sendEmail(to, subject, body);
        });
    }

    @Test
    public void testSendEmailWithEmptySubject() {
        String to = "pankti@gmail.com";
        String subject = "";
        String body = "Do Verify your email click on this email";

        Assertions.assertDoesNotThrow(() -> {
            emailService.sendEmail(to, subject, body);
        });
    }

    @Test
    public void testSendEmailWithNullBody() {
        String to = "pankti@gmail.com";
        String subject = "Academic Planner Login Verification";
        String body = null;

        Assertions.assertDoesNotThrow(() -> {
            emailService.sendEmail(to, subject, body);
        });
    }
}
