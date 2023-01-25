package com.group13.academicplannerbackend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

public class FixedEventTest {
    private FixedEvent fixedEvent;

    @BeforeEach
    public void setUp() {
        fixedEvent = new FixedEvent();
    }

    @Test
    public void testSetAndGetId() {
        fixedEvent.setId(1L);
        assertEquals(1L, fixedEvent.getId());
    }

    @Test
    public void testSetAndGetName() {
        fixedEvent.setName("Test Event");
        assertEquals("Test Event", fixedEvent.getName());
    }

    @Test
    public void testSetAndGetStartDate() {
        LocalDate date = LocalDate.of(2023, 4, 9);
        fixedEvent.setStartDate(date);
        assertEquals(date, fixedEvent.getStartDate());
    }

    @Test
    public void testSetAndGetEndDate() {
        LocalDate date = LocalDate.of(2023, 4, 10);
        fixedEvent.setEndDate(date);
        assertEquals(date, fixedEvent.getEndDate());
    }

    @Test
    public void testSetAndGetStartTime() {
        LocalTime time = LocalTime.of(12, 30);
        fixedEvent.setStartTime(time);
        assertEquals(time, fixedEvent.getStartTime());
    }

    @Test
    public void testSetAndGetEndTime() {
        LocalTime time = LocalTime.of(13, 30);
        fixedEvent.setEndTime(time);
        assertEquals(time, fixedEvent.getEndTime());
    }

    @Test
    public void testSetAndGetIsReschedulable() {
        fixedEvent.setReschedulable(true);
        assertTrue(fixedEvent.isReschedulable());
    }

    @Test
    public void testSetAndGetDetails() {
        fixedEvent.setDetails("Test details");
        assertEquals("Test details", fixedEvent.getDetails());
    }

    @Test
    public void testSetAndGetIsRepeat() {
        fixedEvent.setRepeat(true);
        assertTrue(fixedEvent.isRepeat());
    }

    @Test
    public void testSetAndGetEventCategory() {
        fixedEvent.setEventCategory(EventCategory.ASSIGNMENT);
        assertEquals(EventCategory.ASSIGNMENT, fixedEvent.getEventCategory());
    }

    @Test
    public void testSetAndGetRepeatEvent() {
        RepeatEvent repeatEvent = new RepeatEvent();
        repeatEvent.setEvent(fixedEvent);
        fixedEvent.setRepeatEvent(repeatEvent);
        assertEquals(repeatEvent, fixedEvent.getRepeatEvent());
    }

    @Test
    public void testSetAndGetUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        fixedEvent.setUser(user);
        assertEquals(user, fixedEvent.getUser());
    }

    @Test
    public void testGetFixedEventName() {
        fixedEvent.setName("Test Event");
        assertEquals("Test Event", fixedEvent.getFixedEventName());
    }
}
