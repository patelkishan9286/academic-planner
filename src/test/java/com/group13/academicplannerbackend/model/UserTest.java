package com.group13.academicplannerbackend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    public void testSetAndGetId() {
        user.setId(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    public void testSetAndGetEmail() {
        user.setEmail("testuser@example.com");
        assertEquals("testuser@example.com", user.getEmail());
    }

    @Test
    public void testSetAndGetFirstName() {
        user.setFirstName("Test");
        assertEquals("Test", user.getFirstName());
    }

    @Test
    public void testSetAndGetLastName() {
        user.setLastName("User");
        assertEquals("User", user.getLastName());
    }

    @Test
    public void testSetAndGetPasswordHash() {
        user.setPasswordHash("password123");
        assertEquals("password123", user.getPasswordHash());
    }

    @Test
    public void testSetAndGetUserMeta() {
        UserMeta userMeta = new UserMeta();
        user.setUserMeta(userMeta);
        assertEquals(userMeta, user.getUserMeta());
    }

    @Test
    public void testSetAndGetFixedEvents() {
        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
        List<FixedEvent> fixedEvents = new ArrayList<>();
        fixedEvents.add(fixedEvent);
        user.setFixedEvents(fixedEvents);
        assertEquals(fixedEvents, user.getFixedEvents());
    }

    @Test
    public void testSetAndGetVariableEvents() {
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setId(1L);
        List<VariableEvent> variableEvents = new ArrayList<>();
        variableEvents.add(variableEvent);
        user.setVariableEvents(variableEvents);
        assertEquals(variableEvents, user.getVariableEvents());
    }

    @Test
    public void testGetAuthorities() {
        assertNull(user.getAuthorities());
    }

    @Test
    public void testGetUsername() {
        user.setEmail("testuser@example.com");
        assertEquals("testuser@example.com", user.getUsername());
    }

    @Test
    public void testIsAccountNonExpired() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    public void testIsAccountNonLocked() {
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    public void testIsCredentialsNonExpired() {
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    public void testIsEnabled() {
        assertTrue(user.isEnabled());
    }
}
