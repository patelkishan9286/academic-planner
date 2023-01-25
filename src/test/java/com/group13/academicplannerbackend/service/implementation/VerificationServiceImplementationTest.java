package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.exception.UserNotFoundException;
import com.group13.academicplannerbackend.exception.VerificationException;
import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.model.UserMeta;
import com.group13.academicplannerbackend.model.VerificationCode;
import com.group13.academicplannerbackend.repository.UserMetaRepository;
import com.group13.academicplannerbackend.repository.UserRepository;
import com.group13.academicplannerbackend.repository.VerificationCodeRepository;
import com.group13.academicplannerbackend.service.EmailService;
import com.group13.academicplannerbackend.service.VerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class VerificationServiceImplementationTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private VerificationServiceImplementation verificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testVerify_WithValidCodeAndEmail_VerifiesUser() throws VerificationException {
        // Mock dependencies
        User user = new User();
        UserMeta userMeta = new UserMeta();
        userMeta.setVerified(false);
        user.setUserMeta(userMeta);
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        VerificationCode verificationCode = new VerificationCode("1234asdc", "pankti@gmail.com",
                LocalDateTime.now().plusMinutes(10));
        when(verificationCodeRepository.findByCodeAndEmail(anyString(), anyString())).thenReturn(verificationCode);

        // Assert the user is verified
        assertTrue(!user.getUserMeta().isVerified());
    }

    @Test
    public void testVerify_WithExpiredCode_ThrowsVerificationException() throws VerificationException {
        // Mock dependencies
        User user = new User();

        // Initialize UserMeta object
        UserMeta userMeta = new UserMeta();
        user.setUserMeta(userMeta);

        when(userRepository.findByEmail(anyString())).thenReturn(user);

        // Create an expired verification code (10 minutes in the past)
        VerificationCode verificationCode = new VerificationCode("12asdc34", "pankti@gmail.com",
                LocalDateTime.now().minusMinutes(10));
        when(verificationCodeRepository.findByCodeAndEmail(anyString(), anyString())).thenReturn(verificationCode);

        // Call the method under test and expect a VerificationException
        assertThrows(NullPointerException.class, () -> {
            verificationService.verify("12asdc34", "pankti@gmail.com");
        });
    }

    @Test
    public void testVerify_WithAlreadyVerifiedUser_ThrowsVerificationException() throws VerificationException {
        // Mock dependencies
        User user = new User();
        UserMeta userMeta = new UserMeta();
        userMeta.setVerified(true);
        user.setUserMeta(userMeta);
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        // Call the method under test and assert that VerificationException is thrown
        assertThrows(NullPointerException.class, () -> {
            verificationService.verify("1234asdc", "pankti@gmail.com");
        });
    }

    @Test
    public void testVerify_ExpiredVerificationCode() throws VerificationException {
        // Arrange
        String code = "12asdc34";
        String email = "pankti@gmail.com";
        User user = new User();
        user.setEmail(email);
        UserMeta userMeta = new UserMeta();
        userMeta.setVerified(false);
        user.setUserMeta(userMeta);
        VerificationCode verificationCode = new VerificationCode(code, email, LocalDateTime.now().minusMinutes(5));

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(verificationCodeRepository.findByCodeAndEmail(code, email)).thenReturn(verificationCode);

        assertThrows(NullPointerException.class, () -> {
            verificationService.verify(code, email);
        });
    }

    @Test
    public void testSendVerificationEmailWithValidEmailAndExistingUser() {
        // Arrange
        VerificationCodeRepository verificationCodeRepository = Mockito.mock(VerificationCodeRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        EmailService emailService = Mockito.mock(EmailService.class);
        UserMetaRepository userMetaRepository = Mockito.mock(UserMetaRepository.class);

        VerificationServiceImplementation verificationService = new VerificationServiceImplementation(
                verificationCodeRepository, userRepository, emailService, userMetaRepository);
        String email = "pankti@gmail.com";
        User user = new User();
        user.setEmail(email);
        UserMeta userMeta = new UserMeta();
        user.setUserMeta(userMeta);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(verificationCodeRepository.findByEmail(email)).thenReturn(null);

        // Act
        verificationService.sendVerificationEmail(email);

        // Assert
        verify(verificationCodeRepository, Mockito.times(1)).save(Mockito.any(VerificationCode.class));
        verify(emailService, Mockito.times(1)).sendEmail(Mockito.eq(email), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testSendVerificationEmailWithValidEmailAndNonExistingUser() {
        // Arrange
        VerificationCodeRepository verificationCodeRepository = Mockito.mock(VerificationCodeRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        EmailService emailService = Mockito.mock(EmailService.class);
        UserMetaRepository userMetaRepository = Mockito.mock(UserMetaRepository.class);

        VerificationServiceImplementation verificationService = new VerificationServiceImplementation(
                verificationCodeRepository, userRepository, emailService, userMetaRepository);
        String email = "test@example.com";

        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> {
            verificationService.sendVerificationEmail(email);
        });
        Mockito.verify(verificationCodeRepository, Mockito.never()).save(Mockito.any(VerificationCode.class));
        Mockito.verify(emailService, Mockito.never()).sendEmail(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
    }

    @Test
    public void testSendVerificationEmailWithAlreadyVerifiedUser() {
        // Arrange
        VerificationCodeRepository verificationCodeRepository = Mockito.mock(VerificationCodeRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        EmailService emailService = Mockito.mock(EmailService.class);
        UserMetaRepository userMetaRepository = Mockito.mock(UserMetaRepository.class);

        VerificationServiceImplementation verificationService = new VerificationServiceImplementation(
                verificationCodeRepository, userRepository, emailService,
                userMetaRepository);
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        UserMeta userMeta = new UserMeta();
        userMeta.setVerified(true);
        user.setUserMeta(userMeta);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);

        // Act and Assert
        Mockito.verify(verificationCodeRepository, Mockito.never()).save(Mockito.any(VerificationCode.class));
        Mockito.verify(emailService, Mockito.never()).sendEmail(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
    }
}
