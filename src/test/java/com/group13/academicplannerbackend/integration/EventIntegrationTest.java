package com.group13.academicplannerbackend.integration;

import com.group13.academicplannerbackend.controller.EventController;
import com.group13.academicplannerbackend.model.*;
import com.group13.academicplannerbackend.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class EventIntegrationTest {
    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    void testUpdateFixedEvent() {
        FixedEvent fixedEvent = new FixedEvent();
        UpdateEventStatus status = UpdateEventStatus.SUCCESS;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("test@example.com");
        when(eventService.updateFixedEvent(fixedEvent, mockPrincipal)).thenReturn(status);
        ResponseEntity<String> response = eventController.updateFixedEvent(fixedEvent, mockPrincipal);
        verify(eventService).updateFixedEvent(fixedEvent, mockPrincipal);
        assertEquals(ResponseEntity.ok("Event updated successfully"), response);
    }

    @Test
    public void testDeleteFixedEventById() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.deleteFixedEvent(anyLong(), any(Principal.class))).thenReturn(DeleteEventStatus.SUCCESS);
        ResponseEntity<String> response = eventController.deleteFixedEventById(1, mockPrincipal);
        assertAll("Response",
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK"),
                () -> assertEquals("Event deleted successfully", response.getBody(), "Response body should match")
        );
    }

    @Test
    public void testCreateVariableEvent() {
        VariableEvent variableEvent = new VariableEvent();
        Principal mockPrincipal = Mockito.mock(Principal.class);
        List<EventDTO> emptyUnscheduledEvents = Collections.emptyList();
        when(eventService.createVariableEvent(variableEvent, mockPrincipal)).thenReturn(emptyUnscheduledEvents);
        ResponseEntity<Map> response = eventController.createVariableEvent(variableEvent, mockPrincipal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals("Variable Event created successfully", responseBody.get("message"));
        verify(eventService, times(1)).createVariableEvent(variableEvent, mockPrincipal);
    }

    @Test
    public void testDeleteVariableEventById_Success() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");
        when(eventService.deleteVariableEvent(anyLong(), eq(principal))).thenReturn(DeleteEventStatus.SUCCESS);
        ResponseEntity<String> response = eventController.deleteVariableEventById(1, principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event deleted successfully", response.getBody());
        verify(eventService, times(1)).deleteVariableEvent(1L, principal);
    }
}
