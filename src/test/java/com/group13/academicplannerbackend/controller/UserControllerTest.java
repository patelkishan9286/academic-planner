package com.group13.academicplannerbackend.controller;

import com.group13.academicplannerbackend.exception.UnAuthorizedUserException;
import com.group13.academicplannerbackend.exception.UserNotFoundException;
import com.group13.academicplannerbackend.exception.VerificationException;
import com.group13.academicplannerbackend.model.JwtResponse;
import com.group13.academicplannerbackend.model.ProfileStatus;
import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.model.UserMeta;
import com.group13.academicplannerbackend.repository.UserMetaRepository;
import com.group13.academicplannerbackend.repository.UserRepository;
import com.group13.academicplannerbackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMetaRepository userMetaRepository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
//        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testSignup_Success() {
        // Create a mock User object
        User user = new User();
        user.setEmail("pankti@gmail.com");
        user.setPasswordHash("pankti25");

        // Call the signup method with the mock User object
        doNothing().when(userService).register(user);
        ResponseEntity<String> response = userController.signup(user);

        // Assert that the response has an OK status and the correct message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody());
        verify(userService, times(1)).register(user);
    }

    @Test
    public void testLogin_Success() {
        // Given
        User user = new User();
        user.setEmail("pankti@gmail.com");
        user.setPasswordHash("pankti25");
        JwtResponse jwtResponse = new JwtResponse("testjwttoken", ProfileStatus.UNSET);
        when(userService.loginProcess(user)).thenReturn(jwtResponse);

        // When
        ResponseEntity<?> response = userController.login(user);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtResponse, response.getBody());
        verify(userService, times(1)).loginProcess(user);
    }

    @Test
    public void testLogin_UnAuthorizedUserException() {
        // Given
        User user = new User();
        user.setEmail("pankti@gmail.com");
        user.setPasswordHash("pankti25");
        String errorMessage = "Wrong username or password";
        when(userService.loginProcess(user)).thenThrow(new UnAuthorizedUserException(errorMessage));

        // When
        ResponseEntity<?> response = userController.login(user);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(userService, times(1)).loginProcess(user);
    }

    @Test
    public void testLogin_VerificationException() {
        // Given
        User user = new User();
        user.setEmail("pankti@gmail.com");
        user.setPasswordHash("pankti25");
        String errorMessage = "please varify your email";
        when(userService.loginProcess(user)).thenThrow(new VerificationException(errorMessage));

        // When
        ResponseEntity<?> response = userController.login(user);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(userService, times(1)).loginProcess(user);
    }

    @Test
    public void testUpdateProfileStatus_Success() {
        // Arrange
        String userEmail = "pankti@gmail.com";
        UserMeta userMeta = new UserMeta();
        userMeta.setProfileStatus(ProfileStatus.UNSET);
        User user = new User();
        user.setEmail(userEmail);
        user.setUserMeta(userMeta);

        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(userMetaRepository.save(any(UserMeta.class))).thenReturn(userMeta);

        // Act
        ResponseEntity<?> response = userController.updateProfileStatus(() -> userEmail);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Profile status updated to SET", response.getBody());
    }

    @Test
    void testUpdateProfileStatus_Failure() throws Exception {
        // Given
        String userEmail = "pankti@gmail.com";
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(userEmail);
        doThrow(new UserNotFoundException("User not found with email " + userEmail))
                .when(userService).updateProfileStatus(userEmail);

        // When
        ResponseEntity<?> response = userController.updateProfileStatus(mockPrincipal);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status code should be INTERNAL_SERVER_ERROR");
        assertEquals("Failed to update profile status: User not found with email " + userEmail, response.getBody(), "Response body should match");
    }
}
