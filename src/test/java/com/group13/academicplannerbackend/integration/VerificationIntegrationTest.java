package com.group13.academicplannerbackend.integration;

import com.group13.academicplannerbackend.controller.VerificationController;
import com.group13.academicplannerbackend.service.UserService;
import com.group13.academicplannerbackend.service.VerificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class VerificationIntegrationTest {
    private MockMvc mockMvc;

    @InjectMocks
    private VerificationController verificationController;

    @Mock
    private VerificationService verificationService;

    @Mock
    private UserService userService; // You need this because JWTRequestFilter depends on UserDetailsService

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(verificationController).build();
    }

    @Test
    public void testSuccessfulVerification() throws Exception {
        mockMvc.perform(get("/verify?code=sample_code&email=ms@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Email verification successful")));
    }

    @Test
    public void testResendVerificationEmailSuccess() {
        String email = "ms@gmail.com";
        Mockito.doNothing().when(verificationService).sendVerificationEmail(email);
        verificationController.resendVerificationEmail(email);
        Mockito.verify(verificationService, Mockito.times(1)).sendVerificationEmail(email);
    }
}
