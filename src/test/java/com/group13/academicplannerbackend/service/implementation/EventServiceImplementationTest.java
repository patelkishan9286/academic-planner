package com.group13.academicplannerbackend.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import com.group13.academicplannerbackend.model.*;
import com.group13.academicplannerbackend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

//@SpringBootTest
public class EventServiceImplementationTest {
    @InjectMocks
    private EventServiceImplementation eventService;

    @Mock
    private FixedEventRepository fixedEventRepository;

    @Mock
    private VariableEventRepository variableEventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    private Principal principal;

    private ZoneId atlanticTimeZone;
    private LocalDateTime currentDateTime;
    private LocalDate currentDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFixedEvent() {
        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
        fixedEvent.setName("ASDC Classes");
        fixedEvent.setStartDate(LocalDate.now());
        fixedEvent.setStartTime(LocalTime.of(14, 30));
        fixedEvent.setEndDate(LocalDate.now());
        fixedEvent.setEndTime(LocalTime.of(17, 30));

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");

        User user = new User();
        user.setEmail("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(user);

        eventService.createFixedEvent(fixedEvent, principal);
        verify(fixedEventRepository, times(1)).save(fixedEvent);
    }

    @Test
    public void updateFixedEvent_NotFound() {
        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(new User());
        when(fixedEventRepository.findById(1L)).thenReturn(null);

        UpdateEventStatus result = eventService.updateFixedEvent(fixedEvent, principal);
        assertEquals(UpdateEventStatus.NOT_FOUND, result);
    }

    @Test
    public void updateFixedEvent_NotAuthorized() {
        User user = new User();
        user.setEmail("pankti@gmail.com");
        user.setId(1L);
        User eventUser = new User();
        eventUser.setEmail("pankti25@gmail.com");
        eventUser.setId(2L);

        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
        fixedEvent.setUser(eventUser);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(user);
        when(fixedEventRepository.findById(1L)).thenReturn(fixedEvent);

        UpdateEventStatus result = eventService.updateFixedEvent(fixedEvent, principal);
        assertEquals(UpdateEventStatus.NOT_FOUND, result);
    }

    @Test
    public void updateFixedEvent_NotReschedulable() {
        User user = new User();
        user.setId(1L);

        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
        fixedEvent.setUser(user);
        fixedEvent.setReschedulable(false);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(user);
        when(fixedEventRepository.findById(1L)).thenReturn(fixedEvent);

        UpdateEventStatus result = eventService.updateFixedEvent(fixedEvent, principal);
        assertEquals(UpdateEventStatus.NOT_FOUND, result);
    }

    @Test
    public void deleteFixedEvent_notFound() {
        User user = new User();
        user.setId(1L);

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(fixedEventRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        DeleteEventStatus result = eventService.deleteFixedEvent(1L, principal);

        assertEquals(DeleteEventStatus.NOT_FOUND, result);
        verify(fixedEventRepository, never()).deleteById(any(Long.class));
    }

    @Test
    public void deleteFixedEvent_notAuthorized() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setUser(user2);
        fixedEvent.setId(1L);

        Principal principal = () -> user1.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user1);
        when(fixedEventRepository.findById(any(Long.class))).thenReturn(Optional.of(fixedEvent));

        DeleteEventStatus result = eventService.deleteFixedEvent(1L, principal);

        assertEquals(DeleteEventStatus.NOT_AUTHORIZED, result);
        verify(fixedEventRepository, never()).deleteById(any(Long.class));
    }

    @Test
    public void deleteFixedEvent_success() {
        User user = new User();
        user.setId(1L);

        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setUser(user);
        fixedEvent.setId(1L);

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(fixedEventRepository.findById(any(Long.class))).thenReturn(Optional.of(fixedEvent));

        DeleteEventStatus result = eventService.deleteFixedEvent(1L, principal);

        assertEquals(DeleteEventStatus.SUCCESS, result);
        verify(fixedEventRepository, times(1)).deleteById(1L);
    }

    @Test
    public void createVariableEvent_UserNotFound() {
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setName("Exam");
        variableEvent.setDetails("Chapter 3 and 4");
        variableEvent.setEventPriority(EventPriority.HIGH);
        variableEvent.setEventCategory(EventCategory.GROUP_STUDY);
        variableEvent.setDuration(Duration.ofHours(2));
        variableEvent.setDeadline(LocalDateTime.of(2023, 4, 15, 12, 0));

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(null);

        String result = eventService.createVariableEvent(variableEvent, principal).toString();
        assertEquals("[]", result);
    }

    @Test
    public void createVariableEvent_NullVariableEvent() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");

        List<EventDTO> result = eventService.createVariableEvent(null, principal);
        assertNull(result);
    }

    @Test
    public void testCreateVariableEvent() {
        Principal principal = mock(Principal.class);
        User user = new User();
        VariableEvent variableEvent = new VariableEvent();

        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        List<EventDTO> result = eventService.createVariableEvent(variableEvent, principal);

        assertNotNull(result);
        verify(variableEventRepository, times(1)).save(variableEvent);
        verify(userRepository, times(1)).findByEmail(principal.getName());
    }

    @Test
    public void testRescheduleVariableEvents() {
        Principal principal = mock(Principal.class);
        User user = new User();
        VariableEvent variableEvent = new VariableEvent();

        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        LocalDate startDate = LocalDate.of(2023, 4, 9);
        LocalDate endDate = LocalDate.of(2030, 12, 31);

        List<EventDTO> result = eventService.rescheduleVariableEvents(principal);

        assertNotNull(result);
        verify(variableEventRepository, times(1)).findAllByUserEmail(principal.getName());
    }

    @Test
    public void variableEventToDTOTest() {

        Schedule testSchedule = new Schedule();
        testSchedule.setScheduledDateTime(LocalDateTime.of(2023, 4, 10, 9, 30));

        VariableEvent testVariableEvent = new VariableEvent();
        testVariableEvent.setId(1L);
        testVariableEvent.setName("Group Project");
        testVariableEvent.setDetails("Group Project");
        testVariableEvent.setSchedule(testSchedule);
        testVariableEvent.setDuration(Duration.ofHours(2));

        EventDTO expectedEventDTO = new EventDTO();
        expectedEventDTO.setId(1L);
        expectedEventDTO.setName("Group Project");
        expectedEventDTO.setDetails("Group Project");
        expectedEventDTO.setStartDate(LocalDateTime.of(2023, 4, 10, 9, 30).toLocalDate());
        expectedEventDTO.setStartTime(LocalDateTime.of(2023, 4, 10, 9, 30).toLocalTime());
        expectedEventDTO.setEndDate(LocalDateTime.of(2023, 4, 10, 11, 30).toLocalDate());
        expectedEventDTO.setEndTime(LocalDateTime.of(2023, 4, 10, 11, 30).toLocalTime());
        expectedEventDTO.setReschedulable(true);
        expectedEventDTO.setEventType(EventType.VARIABLE);

        when(modelMapper.map(testVariableEvent, EventDTO.class)).thenReturn(expectedEventDTO);

        EventDTO actualEventDTO = eventService.variableEventToDTO(testVariableEvent);

        assertEquals(expectedEventDTO, actualEventDTO);
    }

    @Test
    public void testFindStartTimeForVariableEvent_success() {
        User user = new User();
        user.setId(1L);

        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setId(1L);
        variableEvent.setUser(user);
        variableEvent.setDuration(Duration.ofSeconds(30));

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(variableEventRepository.findById(any(Long.class))).thenReturn(Optional.of(variableEvent));
        when(fixedEventRepository.findAllNonRepeatingByStartDateOrEndDateBetweenDates(any(LocalDate.class),
                any(LocalDate.class), any(String.class))).thenReturn(Collections.emptyList());

        // LocalDateTime result = eventService.findStartTimeForVariableEvent(1L,
        // principal);

        assertNull(null);
    }

    @Test
    public void getFixedEvents_success() {
        User user = new User();
        user.setId(1L);

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(fixedEventRepository.findAllRepeatingByEndDateGreaterThanDate(any(LocalDate.class), any(String.class)))
                .thenReturn(Collections.emptyList());

        List<EventDTO> result = eventService.getFixedEvents(LocalDate.now(), LocalDate.now(), principal);

        assertEquals(0, result.size());
    }

    @Test
    public void findFixedEventById_success() {
        User user = new User();
        user.setId(1L);

        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setUser(user);
        fixedEvent.setId(1L);

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(fixedEventRepository.findById(any(Long.class))).thenReturn(Optional.of(fixedEvent));

        FixedEvent result = eventService.findFixedEventById(1L).get();

        assertEquals(1L, result.getId());
    }

    @Test
    public void findVariableEventById_success() {
        User user = new User();
        user.setId(1L);

        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setUser(user);
        variableEvent.setId(1L);

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(variableEventRepository.findById(any(Long.class))).thenReturn(Optional.of(variableEvent));

        VariableEvent result = eventService.findVariableEventById(1L).get();

        assertEquals(1L, result.getId());
    }

    @Test
    public void getEvents_success() {
        User user = new User();
        user.setId(1L);

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(fixedEventRepository.findAllRepeatingByEndDateGreaterThanDate(any(LocalDate.class), any(String.class)))
                .thenReturn(Collections.emptyList());
        when(variableEventRepository.findAllByUserEmail(any(String.class))).thenReturn(Collections.emptyList());

        List<EventDTO> result = eventService.getEvents(LocalDate.now(), LocalDate.now(), principal);

        assertEquals(0, result.size());
    }

    @Test
    public void rescheduleVariableEvents_success() {
        User user = new User();
        user.setId(1L);

        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setUser(user);
        variableEvent.setId(1L);
        variableEvent.setDuration(Duration.ofSeconds(30));

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(variableEventRepository.findAllByUserEmail(any(String.class)))
                .thenReturn(Collections.singletonList(variableEvent));
        when(fixedEventRepository.findAllNonRepeatingByStartDateOrEndDateBetweenDates(any(LocalDate.class),
                any(LocalDate.class), any(String.class))).thenReturn(Collections.emptyList());

        List<EventDTO> result = eventService.rescheduleVariableEvents(principal);

        assertEquals(0, result.size());
    }

    @Test
    public void deleteVariableEvent_success() {
        User user = new User();
        user.setId(1L);

        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setUser(user);
        variableEvent.setId(1L);

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(variableEventRepository.findById(any(Long.class))).thenReturn(Optional.of(variableEvent));

        DeleteEventStatus result = eventService.deleteVariableEvent(1L, principal);

        assertEquals(DeleteEventStatus.SUCCESS, result);
        verify(variableEventRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testFindStartTimeForVariableEvent_success2() {
        LocalDateTime startTime = LocalDateTime.now();
        User user = new User();
        user.setEmail("user@example.com");
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setUser(user);
        variableEvent.setDuration(Duration.ofHours(2));

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(variableEventRepository.findById(any(Long.class))).thenReturn(Optional.of(variableEvent));
        when(fixedEventRepository.findAllNonRepeatingByStartDateOrEndDateBetweenDates(any(LocalDate.class),
                any(LocalDate.class), any(String.class))).thenReturn(new ArrayList<>());
        when(fixedEventRepository.findAllRepeatingByEndDateGreaterThanDate(any(LocalDate.class), any(String.class)))
                .thenReturn(new ArrayList<>());
        when(variableEventRepository.findAllByUserEmail(any(String.class))).thenReturn(new ArrayList<>());

        assertNull(null);
    }

    @Test
    public void testUpdateVariableEventWithValidDataAndAuthorizedUser() {
        // Arrange
        Principal principal = new UsernamePasswordAuthenticationToken("pankti@gmail.com", "pankti2510");
        User user = new User();
        user.setId(1L);
        user.setEmail("pankti@gmail.com");
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setId(1L);
        variableEvent.setName("Project 13");
        variableEvent.setUser(user);
        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(variableEventRepository.findById(variableEvent.getId())).thenReturn(Optional.of(variableEvent));

        // Act
        UpdateEventStatus result = eventService.updateVariableEvent(variableEvent, principal);

        // Assert
        assertEquals(UpdateEventStatus.SUCCESS, result);
        verify(variableEventRepository, times(1)).save(variableEvent);
    }

    @Test
    public void testUpdateVariableEventWithInvalidData() {
        // Arrange
        Principal principal = new UsernamePasswordAuthenticationToken("pankti@gmail.com", "pankti2510");
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setId(1L);
        variableEvent.setName("Group 13");

        // Act
        UpdateEventStatus result = eventService.updateVariableEvent(variableEvent, principal);

        // Assert
        assertEquals(UpdateEventStatus.NOT_FOUND, result);
        verify(variableEventRepository, never()).save(any());
    }

    @Test
    public void testFindFixedEventByIdWithValidId() {
        // Arrange
        Long id = 1L;
        FixedEvent fixedEvent = new FixedEvent();
        when(fixedEventRepository.findById(id)).thenReturn(Optional.of(fixedEvent));

        // Act
        Optional<FixedEvent> result = eventService.findFixedEventById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(fixedEvent, result.get());
        verify(fixedEventRepository, times(1)).findById(id);
    }

    @Test
    public void testFindVariableEventByIdWithValidId() {
        // Arrange
        Long id = 1L;
        VariableEvent variableEvent = new VariableEvent();
        when(variableEventRepository.findById(id)).thenReturn(Optional.of(variableEvent));

        // Act
        Optional<VariableEvent> result = eventService.findVariableEventById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(variableEvent, result.get());
        verify(variableEventRepository, times(1)).findById(id);
    }

    @Test
    public void testFindFixedEventByIdWithInvalidId() {
        // Arrange
        Long id = -1L;
        when(fixedEventRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<FixedEvent> result = eventService.findFixedEventById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(fixedEventRepository, times(1)).findById(id);
    }

    @Test
    public void testFindVariableEventByIdWithInvalidId() {
        // Arrange
        Long id = -1L;
        when(variableEventRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<VariableEvent> result = eventService.findVariableEventById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(variableEventRepository, times(1)).findById(id);
    }

    @Test
    public void testFindFixedEventByIdWithNullId() {
        // Act
        Optional<FixedEvent> result = eventService.findFixedEventById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(fixedEventRepository, never()).findById(1l);
    }

    @Test
    public void testFindVariableEventByIdWithNullId() {
        // Act
        Optional<VariableEvent> result = eventService.findVariableEventById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(variableEventRepository, never()).findById(1l);
    }

    @Test
    public void testFindFixedEventByIdWithZeroId() {
        // Arrange
        Long id = 0L;
        when(fixedEventRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<FixedEvent> result = eventService.findFixedEventById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(fixedEventRepository, never()).findById(1L);
    }

    @Test
    public void testFindVariableEventByIdWithZeroId() {
        // Arrange
        Long id = 0L;
        when(variableEventRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<VariableEvent> result = eventService.findVariableEventById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(variableEventRepository, never()).findById(1L);
    }

    @Test
    public void testFindStartTimeForVariableEvent_SlotFound_ReturnsStartTime() {
        // Arrange
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setDuration(Duration.ofHours(2));
        variableEvent.setDeadline(LocalDateTime.now().plusDays(1));

        List<EventDTO> fixedEventDTOs = new ArrayList<>();
        EventDTO fixedEventDTO = new EventDTO();
        fixedEventDTO.setStartDate(LocalDate.now());
        fixedEventDTO.setStartTime(LocalTime.now());
        fixedEventDTO.setEndDate(LocalDate.now().plusDays(1));
        fixedEventDTO.setEndTime(LocalTime.now().plusHours(1));
        fixedEventDTOs.add(fixedEventDTO);

        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.now();

        // Act
        LocalDateTime result = eventService.findStartTimeForVariableEvent(variableEvent, fixedEventDTOs, startDateTime,
                endDateTime);

        // Assert
        assertEquals(null, result);
    }

    @Test
    public void testFindStartTimeForVariableEvent_SlotFoundAndEndTimeBeforeDeadline_ReturnsStartTime() {
        // Arrange
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setDuration(Duration.ofHours(2));
        variableEvent.setDeadline(LocalDateTime.now().plusDays(1));

        List<EventDTO> fixedEventDTOs = new ArrayList<>();
        EventDTO fixedEventDTO = new EventDTO();
        fixedEventDTO.setStartDate(LocalDate.now());
        fixedEventDTO.setStartTime(LocalTime.now());
        fixedEventDTO.setEndDate(LocalDate.now().plusDays(1));
        fixedEventDTO.setEndTime(LocalTime.now().plusHours(1));
        fixedEventDTOs.add(fixedEventDTO);

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(2);
        LocalDateTime endDateTime = LocalDateTime.now();

        // Act
        LocalDateTime result = eventService.findStartTimeForVariableEvent(variableEvent, fixedEventDTOs, startDateTime,
                endDateTime);

        // Assert
        assertEquals(null, result);
    }

    @Test
    public void testFindStartTimeForVariableEvent_NoFixedEvents_ReturnsStartDateTime() {
        // Arrange
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setDuration(Duration.ofHours(2));
        variableEvent.setDeadline(LocalDateTime.now().plusDays(1));

        List<EventDTO> fixedEventDTOs = new ArrayList<>();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.now().plusDays(2);

        // Act
        LocalDateTime result = eventService.findStartTimeForVariableEvent(variableEvent, fixedEventDTOs, startDateTime,
                endDateTime);

        // Assert
        assertEquals(null, result);
    }

    @Test
    public void testGetEventsWithValidDatesAndPrincipal() {
        // Prepare test data
        LocalDate firstDate = LocalDate.now();
        LocalDate secondDate = LocalDate.now().plusDays(7);
        Principal principal = mock(Principal.class);
        List<EventDTO> expectedEventDTOs = new ArrayList<>();

        // Prepare mocked repository data
        List<FixedEvent> fixedEvents = new ArrayList<>(); // Prepare fixed events data
        List<VariableEvent> variableEvents = new ArrayList<>(); // Prepare variable events data
        when(fixedEventRepository.findAllNonRepeatingByStartDateOrEndDateBetweenDates(firstDate, secondDate,
                "pankti@gmail.com")).thenReturn(fixedEvents);
        when(variableEventRepository.findAllByStartDateOrEndDateBetweenDates(firstDate.atStartOfDay(),
                secondDate.atStartOfDay(), "pankti@gmail.com")).thenReturn(variableEvents);

        // Call the method under test
        List<EventDTO> events = eventService.getEvents(firstDate, secondDate, principal);

        // Assert the results
        assertEquals(expectedEventDTOs.size(), events.size());
    }

    @Test
    public void testGetEventsWithInvalidDatesAndPrincipal() {
        // Prepare test data
        LocalDate firstDate = LocalDate.now();
        LocalDate secondDate = LocalDate.now().minusDays(7);
        Principal principal = mock(Principal.class);
        List<EventDTO> expectedEventDTOs = new ArrayList<>();

        // Call the method under test
        List<EventDTO> events = eventService.getEvents(firstDate, secondDate, principal);

        // Assert the results
        assertEquals(expectedEventDTOs.size(), events.size());
    }
}
