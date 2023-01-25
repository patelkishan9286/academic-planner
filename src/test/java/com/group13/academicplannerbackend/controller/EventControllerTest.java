package com.group13.academicplannerbackend.controller;

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
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventControllerTest {

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
    void testUpdateFixedEvent_Success() {
        // Given
        FixedEvent fixedEvent = new FixedEvent();
        UpdateEventStatus status = UpdateEventStatus.SUCCESS;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("test@example.com");
        when(eventService.updateFixedEvent(fixedEvent, mockPrincipal)).thenReturn(status);

        // When
        ResponseEntity<String> response = eventController.updateFixedEvent(fixedEvent, mockPrincipal);

        // Then
        verify(eventService).updateFixedEvent(fixedEvent, mockPrincipal);
        assertEquals(ResponseEntity.ok("Event updated successfully"), response);
    }

    @Test
    void testUpdateFixedEvent_NotReschedulable() {
        // Given
        FixedEvent fixedEvent = new FixedEvent();
        UpdateEventStatus status = UpdateEventStatus.NOT_RESCHEDULABLE;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.updateFixedEvent(fixedEvent, mockPrincipal)).thenReturn(status);

        // When
        ResponseEntity<String> response = eventController.updateFixedEvent(fixedEvent, mockPrincipal);

        // Then
        verify(eventService).updateFixedEvent(fixedEvent, mockPrincipal);
        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Event is not reschedulable"), response);
    }

    @Test
    void testUpdateFixedEvent_NotFound() {
        // Given
        FixedEvent fixedEvent = new FixedEvent();
        UpdateEventStatus status = UpdateEventStatus.NOT_FOUND;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.updateFixedEvent(fixedEvent, mockPrincipal)).thenReturn(status);

        // When
        ResponseEntity<String> response = eventController.updateFixedEvent(fixedEvent, mockPrincipal);

        // Then
        verify(eventService).updateFixedEvent(fixedEvent, mockPrincipal);
        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    void testUpdateFixedEvent_NotAuthorized() {
        // Given
        FixedEvent fixedEvent = new FixedEvent();
        UpdateEventStatus status = UpdateEventStatus.NOT_AUTHORIZED;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.updateFixedEvent(fixedEvent, mockPrincipal)).thenReturn(status);

        // When
        ResponseEntity<String> response = eventController.updateFixedEvent(fixedEvent, mockPrincipal);

        // Then
        verify(eventService).updateFixedEvent(fixedEvent, mockPrincipal);
        assertEquals(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to update this event"), response);
    }

    @Test
    public void testDeleteFixedEventByIdSuccess() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.deleteFixedEvent(anyLong(), any(Principal.class))).thenReturn(DeleteEventStatus.SUCCESS);

        ResponseEntity<String> response = eventController.deleteFixedEventById(1, mockPrincipal);

        assertAll("Response",
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK"),
                () -> assertEquals("Event deleted successfully", response.getBody(), "Response body should match")
        );
    }

    @Test
    public void testDeleteFixedEventByIdNotFound() {
        long eventId = 1;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.deleteFixedEvent(eventId, mockPrincipal)).thenReturn(DeleteEventStatus.NOT_FOUND);

        ResponseEntity<String> response = eventController.deleteFixedEventById(eventId, mockPrincipal);

        assertAll("Response",
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status code should be NOT_FOUND")
        );
    }


    @Test
    public void testDeleteFixedEventByIdForbidden() {
        long eventId = 1;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.deleteFixedEvent(eventId, mockPrincipal)).thenReturn(DeleteEventStatus.NOT_AUTHORIZED);

        ResponseEntity<String> response = eventController.deleteFixedEventById(eventId, mockPrincipal);

        assertAll("Response",
                () -> assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Status code should be FORBIDDEN"),
                () -> assertEquals("Not authorized to delete this event", response.getBody(), "Response body should match")
        );
    }

    @Test
    void testDeleteFixedEventById_NotAuthorized() {
        // Given
        long eventId = 1L;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("test@example.com");
        User currentUser = new User();
        currentUser.setId(1L);
        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(eventId);
        fixedEvent.setUser(currentUser);
        when(eventService.deleteFixedEvent(eventId, mockPrincipal)).thenReturn(DeleteEventStatus.NOT_AUTHORIZED);

        // When
        ResponseEntity<String> response = eventController.deleteFixedEventById(eventId, mockPrincipal);

        // Then
        verify(eventService).deleteFixedEvent(eventId, mockPrincipal);
        assertEquals(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to delete this event"), response);
    }


    @Test
    public void testDeleteFixedEventByIdUnexpected() {
        // Given
        long eventId = 1L;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.deleteFixedEvent(eventId, mockPrincipal)).thenReturn(null);

        // When/Then
        assertThrows(NullPointerException.class, () -> eventController.deleteFixedEventById(eventId, mockPrincipal));
    }


    @Test
    public void testCreateVariableEvent_Success() {
        // Given
        VariableEvent variableEvent = new VariableEvent();
        Principal mockPrincipal = Mockito.mock(Principal.class);
        List<EventDTO> emptyUnscheduledEvents = Collections.emptyList();
        when(eventService.createVariableEvent(variableEvent, mockPrincipal)).thenReturn(emptyUnscheduledEvents);

        // When
        ResponseEntity<Map> response = eventController.createVariableEvent(variableEvent, mockPrincipal);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals("Variable Event created successfully", responseBody.get("message"));
        verify(eventService, times(1)).createVariableEvent(variableEvent, mockPrincipal);
    }

    @Test
    public void testCreateVariableEvent_UnscheduledEvents() {
        // Given
        VariableEvent variableEvent = new VariableEvent();
        Principal mockPrincipal = Mockito.mock(Principal.class);
        List<EventDTO> unscheduledEvents = new ArrayList<>();
        unscheduledEvents.add(new EventDTO());
        unscheduledEvents.add(new EventDTO());
        when(eventService.createVariableEvent(variableEvent, mockPrincipal)).thenReturn(unscheduledEvents);

        // When
        ResponseEntity<Map> response = eventController.createVariableEvent(variableEvent, mockPrincipal);

        // Then
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals("Some variable events couldn't be scheduled.", responseBody.get("message"));
        assertTrue(responseBody.containsKey("unscheduledEvents"), "Response should contain unscheduled events");
        Object unscheduledEventsObj = responseBody.get("unscheduledEvents");
        assertTrue(unscheduledEventsObj instanceof List, "Unscheduled events should be a list");
        List unscheduledEventsList = (List) unscheduledEventsObj;
        assertEquals(2, unscheduledEventsList.size(), "Response should contain two unscheduled events");
        verify(eventService, times(1)).createVariableEvent(variableEvent, mockPrincipal);
    }

    @Test
    public void testUpdateVariableEvent_Success() {
        // Create a mock Principal object for the authenticated user
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");

        // Call the updateVariableEvent method with a valid VariableEvent object and mock Principal
        VariableEvent variableEvent = new VariableEvent();
        UpdateEventStatus updateEventStatus = UpdateEventStatus.SUCCESS;
        when(eventService.updateVariableEvent(variableEvent, principal)).thenReturn(updateEventStatus);

        ResponseEntity<String> response = eventController.updateVariableEvent(variableEvent, principal);

        // Assert that the response has an OK status and the correct message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event updated successfully", response.getBody());
        verify(eventService, times(1)).updateVariableEvent(variableEvent, principal);
    }

    @Test
    public void testUpdateVariableEvent_NotAuthorized() {
        // Create a mock Principal object for the authenticated user
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");

        // Call the updateVariableEvent method with a VariableEvent object not authorized to the user
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setUser(new User());
        UpdateEventStatus updateEventStatus = UpdateEventStatus.NOT_AUTHORIZED;
        when(eventService.updateVariableEvent(variableEvent, principal)).thenReturn(updateEventStatus);

        ResponseEntity<String> response = eventController.updateVariableEvent(variableEvent, principal);

        // Assert that the response has a FORBIDDEN status and the correct message
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Not authorized to update this event", response.getBody());
        verify(eventService, times(1)).updateVariableEvent(variableEvent, principal);
    }

    @Test
    public void testUpdateVariableEvent_NotFound() {
        // Create a mock Principal object for the authenticated user
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");

        // Call the updateVariableEvent method with an invalid VariableEvent object
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setId(1L);
        UpdateEventStatus updateEventStatus = UpdateEventStatus.NOT_FOUND;
        when(eventService.updateVariableEvent(variableEvent, principal)).thenReturn(updateEventStatus);

        ResponseEntity<String> response = eventController.updateVariableEvent(variableEvent, principal);

        // Assert that the response has a NOT FOUND status
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(eventService, times(1)).updateVariableEvent(variableEvent, principal);
    }

    @Test
    public void testDeleteVariableEventById_Success() {
        // Create a mock Principal object for the authenticated user
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");

        // Mock the eventService to return SUCCESS for successful deletion
        when(eventService.deleteVariableEvent(anyLong(), eq(principal))).thenReturn(DeleteEventStatus.SUCCESS);

        // Call the deleteVariableEventById method with a valid event ID and mock Principal
        ResponseEntity<String> response = eventController.deleteVariableEventById(1, principal);

        // Assert that the response has an OK status and the correct message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event deleted successfully", response.getBody());
        verify(eventService, times(1)).deleteVariableEvent(1L, principal);
    }

    @Test
    public void testDeleteVariableEventById_NotFound() {
        // Create a mock Principal object for the authenticated user
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");

        // Mock the eventService to return NOT_FOUND for non-existent event ID
        when(eventService.deleteVariableEvent(anyLong(), eq(principal))).thenReturn(DeleteEventStatus.NOT_FOUND);

        // Call the deleteVariableEventById method with a non-existent event ID and mock Principal
        ResponseEntity<String> response = eventController.deleteVariableEventById(9, principal);

        // Assert that the response has a Not Found status
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(eventService, times(1)).deleteVariableEvent(9L, principal);
    }

    @Test
    public void testDeleteVariableEventByIdNotAuthorized() {
        // Create a mock Principal object for the authenticated user
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("user1");

        // Mock the eventService to return NOT_AUTHORIZED status
        when(eventService.deleteVariableEvent(1L, mockPrincipal)).thenReturn(DeleteEventStatus.NOT_AUTHORIZED);

        // Call the controller method with mocked inputs
        ResponseEntity<String> response = eventController.deleteVariableEventById(1L, mockPrincipal);

        // Assert that the response has a FORBIDDEN status and the correct message
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Not authorized to delete this event", response.getBody());

        // Verify that the eventService was called exactly once with the correct inputs
        verify(eventService, times(1)).deleteVariableEvent(1L, mockPrincipal);
    }


    @Test
    void testRescheduleVariableEvents_NoUnscheduledEvents() {
        // Mock the event service to return an empty list of unscheduled events
        Principal principal = mock(Principal.class);
        when(eventService.rescheduleVariableEvents(any(Principal.class))).thenReturn(Collections.emptyList());

        // Call the rescheduleVariableEvents method
        ResponseEntity<?> response = eventController.rescheduleVariableEvents(principal);

        // Assert that the response has an OK status and the correct message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("Variable events rescheduled successfully", responseBody.get("message"));
    }

    @Test
    public void testRescheduleVariableEvents_UnscheduledEventsFound() {
        // Mock the eventService to return a list of unscheduled events for rescheduling
        Principal principal = mock(Principal.class);
        List<EventDTO> unscheduledEvents = new ArrayList<>();
        EventDTO eventDTO1 = new EventDTO();
        eventDTO1.setName("Event 1");
        EventDTO eventDTO2 = new EventDTO();
        eventDTO2.setName("Event 2");
        unscheduledEvents.add(eventDTO1);
        unscheduledEvents.add(eventDTO2);
        when(eventService.rescheduleVariableEvents(any())).thenReturn(unscheduledEvents);

        // Call the rescheduleVariableEvents method with a valid principal object
        ResponseEntity<?> response = eventController.rescheduleVariableEvents(principal);

        // Assert that the response has a Partial Content status
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());

        // Check that the response body is a Map
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("Some variable events couldn't be rescheduled.", responseBody.get("message"));
        assertEquals(2, ((List<?>) responseBody.get("unscheduledEvents")).size());
    }

    @Test
    public void testGetFixedEventById_Success() {
        // Mock the eventService to return a FixedEvent object
        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
        when(eventService.findFixedEventById(anyLong())).thenReturn(Optional.of(fixedEvent));

        // Call the getFixedEventById method with a valid event ID
        ResponseEntity<FixedEvent> response = eventController.getFixedEventById(1L);

        // Assert that the response has an OK status and the correct body
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fixedEvent, response.getBody());
    }

    @Test
    public void testGetFixedEventById_NotFound() {
        // Mock the eventService to return an empty Optional object
        when(eventService.findFixedEventById(anyLong())).thenReturn(Optional.empty());

        // Call the getFixedEventById method with a non-existent event ID
        ResponseEntity<FixedEvent> response = eventController.getFixedEventById(9L);

        // Assert that the response has a Not Found status
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetVariableEventById_Success() {
        // Create a mock variable event
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setId(1L);
        variableEvent.setName("Test Event");

        // Mock the eventService to return the variable event
        when(eventService.findVariableEventById(anyLong())).thenReturn(Optional.of(variableEvent));

        // Call the getVariableEventById method with the ID of the mock variable event
        ResponseEntity<VariableEvent> response = eventController.getVariableEventById(1L);

        // Assert that the response has an OK status and the correct variable event
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(variableEvent, response.getBody());
        verify(eventService, times(1)).findVariableEventById(1L);
    }

    @Test
    public void testGetVariableEventById_NotFound() {
        // Mock the eventService to return an empty Optional for a non-existent variable event
        when(eventService.findVariableEventById(anyLong())).thenReturn(Optional.empty());

        // Call the getVariableEventById method with an invalid ID
        ResponseEntity<VariableEvent> response = eventController.getVariableEventById(9L);

        // Assert that the response has a Not Found status
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(eventService, times(1)).findVariableEventById(9L);
    }

    @Test
    public void testCreateFixedEvent_Success() {
        // Create a mock fixed event
        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
        fixedEvent.setName("Test Event");

        // Mock the eventService to return an empty list of unscheduled variable events
        when(eventService.createFixedEvent(any(FixedEvent.class), any(Principal.class))).thenReturn(new ArrayList<>());

        // Call the createFixedEvent method with the mock fixed event and a mock principal
        ResponseEntity<Map> response = eventController.createFixedEvent(fixedEvent, mock(Principal.class));

        // Assert that the response has an OK status and the correct message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fixed Event created successfully", response.getBody().get("message"));
        verify(eventService, times(1)).createFixedEvent(any(FixedEvent.class), any(Principal.class));
    }

    @Test
    public void testCreateFixedEvent_UnscheduledVariableEvents() {
        // Create a mock fixed event
        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
        fixedEvent.setName("Test Event");

        // Create a list of mock unscheduled variable events
        List<EventDTO> unscheduledVariableEvents = new ArrayList<>();
        EventDTO unscheduledEvent1 = new EventDTO();
        unscheduledEvent1.setId(2L);
        unscheduledEvent1.setName("Unscheduled Event 1");
        EventDTO unscheduledEvent2 = new EventDTO();
        unscheduledEvent2.setId(3L);
        unscheduledEvent2.setName("Unscheduled Event 2");
        unscheduledVariableEvents.add(unscheduledEvent1);
        unscheduledVariableEvents.add(unscheduledEvent2);

        // Mock the eventService to return the list of unscheduled variable events
        when(eventService.createFixedEvent(any(FixedEvent.class), any(Principal.class))).thenReturn(unscheduledVariableEvents);

        // Call the createFixedEvent method with the mock fixed event and a mock principal
        ResponseEntity<Map> response = eventController.createFixedEvent(fixedEvent, mock(Principal.class));

        // Assert that the response has a PARTIAL CONTENT status, the correct message, and the list of unscheduled events
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertEquals("Fixed Event created successfully but some variable events couldn't be scheduled.", response.getBody().get("message"));
        assertTrue(response.getBody().get("unscheduledEvents") instanceof List);
        assertEquals(unscheduledVariableEvents, response.getBody().get("unscheduledEvents"));
        verify(eventService, times(1)).createFixedEvent(any(FixedEvent.class), any(Principal.class));
    }

    @Test
    public void testGetEvents() {
        // Mock the inputs
        LocalDate firstDate = LocalDate.of(2022, 4, 1);
        LocalDate secondDate = LocalDate.of(2022, 4, 5);
        Principal mockPrincipal = Mockito.mock(Principal.class);

        // Mock the service method
        List<EventDTO> expectedEvents = new ArrayList<>();
        when(eventService.getEvents(firstDate, secondDate, mockPrincipal)).thenReturn(expectedEvents);

        // Call the controller method
        List<EventDTO> actualEvents = eventController.getEvents(firstDate, secondDate, mockPrincipal);

        // Verify the result
        assertEquals(expectedEvents, actualEvents);
        verify(eventService, times(1)).getEvents(firstDate, secondDate, mockPrincipal);
    }

}
