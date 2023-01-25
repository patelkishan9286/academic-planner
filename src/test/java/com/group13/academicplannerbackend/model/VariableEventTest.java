package com.group13.academicplannerbackend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class VariableEventTest {
    private VariableEvent variableEvent;

    @BeforeEach
    public void setUp() {
        variableEvent = new VariableEvent();
    }

    @Test
    public void testSetAndGetId() {
        variableEvent.setId(1L);
        assertEquals(1L, variableEvent.getId());
    }

    @Test
    public void testSetAndGetName() {
        variableEvent.setName("Test Event");
        assertEquals("Test Event", variableEvent.getName());
    }

    @Test
    public void testSetAndGetDetails() {
        variableEvent.setDetails("Test details");
        assertEquals("Test details", variableEvent.getDetails());
    }

    @Test
    public void testSetAndGetEventPriority() {
        variableEvent.setEventPriority(EventPriority.LOW);
        assertEquals(EventPriority.LOW, variableEvent.getEventPriority());
    }

    @Test
    public void testSetAndGetEventCategory() {
        variableEvent.setEventCategory(EventCategory.ASSIGNMENT);
        assertEquals(EventCategory.ASSIGNMENT, variableEvent.getEventCategory());
    }

    @Test
    public void testSetAndGetDuration() {
        Duration duration = Duration.ofHours(1);
        variableEvent.setDuration(duration);
        assertEquals(duration, variableEvent.getDuration());
    }

    @Test
    public void testSetAndGetDeadline() {
        LocalDateTime deadline = LocalDateTime.of(2023, 4, 9, 23, 59);
        variableEvent.setDeadline(deadline);
        assertEquals(deadline, variableEvent.getDeadline());
    }

    @Test
    public void testSetAndGetUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        variableEvent.setUser(user);
        assertEquals(user, variableEvent.getUser());
    }

    @Test
    public void testSetAndGetSchedule() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        schedule.setVariableEvent(variableEvent);
        variableEvent.setSchedule(schedule);
        assertEquals(schedule, variableEvent.getSchedule());
    }

    @Test
    public void testGetVariableEventName() {
        variableEvent.setName("Test Event");
        assertEquals("Test Event", variableEvent.getVariableEventName());
    }
}
