package com.group13.academicplannerbackend.config;

import com.group13.academicplannerbackend.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class JWTRequestFilterTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JWTRequestFilter jwtRequestFilter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldDoFilterInternal_WhenTokenIsNotPresent() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void shouldDoFilterInternal_WhenTokenIsValid() throws ServletException, IOException {
        UserDetails userDetails = mock(UserDetails.class);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = mock(
                UsernamePasswordAuthenticationToken.class);

        String token = "valid_token";
        String username = "user1";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void shouldDoFilterInternal_WhenTokenIsInvalidOrExpired() throws ServletException, IOException {
        String token = "invalid_token";
        String username = "user1";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.getUsernameFromToken(token)).thenThrow(IllegalArgumentException.class)
                .thenThrow(ExpiredJwtException.class);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void shouldDoFilterInternal_WhenAuthenticationAlreadyExistsInContext() throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(mock(UsernamePasswordAuthenticationToken.class));

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void shouldDoFilterInternal_WhenUserDetailsIsNull() throws ServletException, IOException {
        String token = "valid_token";
        String username = "user1";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void shouldDoFilterInternal_WhenTokenDoesNotStartWithBearer() throws ServletException, IOException {
        String token = "invalid_token";

        when(request.getHeader("Authorization")).thenReturn(token);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

}
