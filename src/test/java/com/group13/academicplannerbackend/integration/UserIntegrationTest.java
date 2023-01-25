package com.group13.academicplannerbackend.integration;

import com.group13.academicplannerbackend.controller.UserController;
import com.group13.academicplannerbackend.model.JwtResponse;
import com.group13.academicplannerbackend.model.ProfileStatus;
import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserIntegrationTest {
    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testSignup() {
        User user = new User();
        user.setEmail("pankti@gmail.com");
        user.setPasswordHash("pankti25");
        doNothing().when(userService).register(user);
        ResponseEntity<String> response = userController.signup(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody());
        verify(userService, times(1)).register(user);
    }

    @Test
    public void testLogin() {
        User user = new User();
        user.setEmail("ms@gmail.com");
        user.setPasswordHash("ms97");
        JwtResponse jwtResponse = new JwtResponse("jwttokensample", ProfileStatus.UNSET);
        when(userService.loginProcess(user)).thenReturn(jwtResponse);
        ResponseEntity<?> response = userController.login(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtResponse, response.getBody());
        verify(userService, times(1)).loginProcess(user);
    }
}
