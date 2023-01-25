package com.group13.academicplannerbackend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

public class EventDTOTest {
    private EventDTO eventDTO;

    @BeforeEach
    public void setUp() {
        eventDTO = new EventDTO();
        eventDTO.setId(1L);
        eventDTO.setName("Test Event");
        eventDTO.setStartDate(LocalDate.now());
        eventDTO.setEndDate(LocalDate.now().plusDays(1));
        eventDTO.setStartTime(LocalTime.now());
        eventDTO.setEndTime(LocalTime.now().plusHours(1));
        eventDTO.setReschedulable(true);
        eventDTO.setDetails("Test details");
        eventDTO.setEventPriority(EventPriority.HIGH);
        eventDTO.setRepeat(true);
        eventDTO.setEventCategory(EventCategory.ASSIGNMENT);
        eventDTO.setEventType(EventType.FIXED);
    }

    @Test
    public void testSetAndGetId() {
        eventDTO.setId(1L);
        assertEquals(1L, eventDTO.getId());
    }

    @Test
    public void testSetAndGetName() {
        eventDTO.setName("Test Event");
        assertEquals("Test Event", eventDTO.getName());
    }

    @Test
    public void testSetAndGetStartDate() {
        LocalDate date = LocalDate.of(2023, 4, 9);
        eventDTO.setStartDate(date);
        assertEquals(date, eventDTO.getStartDate());
    }

    @Test
    public void testSetAndGetEndDate() {
        LocalDate date = LocalDate.of(2023, 4, 10);
        eventDTO.setEndDate(date);
        assertEquals(date, eventDTO.getEndDate());
    }

    @Test
    public void testSetAndGetStartTime() {
        LocalTime time = LocalTime.of(12, 30);
        eventDTO.setStartTime(time);
        assertEquals(time, eventDTO.getStartTime());
    }

    @Test
    public void testSetAndGetEndTime() {
        LocalTime time = LocalTime.of(13, 30);
        eventDTO.setEndTime(time);
        assertEquals(time, eventDTO.getEndTime());
    }

    @Test
    public void testSetAndGetIsReschedulable() {
        eventDTO.setReschedulable(true);
        assertTrue(eventDTO.isReschedulable());
    }

    @Test
    public void testSetAndGetDetails() {
        eventDTO.setDetails("Test details");
        assertEquals("Test details", eventDTO.getDetails());
    }

    @Test
    public void testSetAndGetEventPriority() {
        eventDTO.setEventPriority(EventPriority.LOW);
        assertEquals(EventPriority.LOW, eventDTO.getEventPriority());
    }

    @Test
    public void testSetAndGetIsRepeat() {
        eventDTO.setRepeat(true);
        assertTrue(eventDTO.isRepeat());
    }

    @Test
    public void testSetAndGetEventCategory() {
        eventDTO.setEventCategory(EventCategory.LAB);
        assertEquals(EventCategory.LAB, eventDTO.getEventCategory());
    }

    @Test
    public void testSetAndGetEventType() {
        eventDTO.setEventType(EventType.VARIABLE);
        assertEquals(EventType.VARIABLE, eventDTO.getEventType());
    }

}
