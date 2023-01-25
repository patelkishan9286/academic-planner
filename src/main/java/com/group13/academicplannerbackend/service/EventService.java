package com.group13.academicplannerbackend.service;

import com.group13.academicplannerbackend.model.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventService {
    List<EventDTO> createFixedEvent(FixedEvent fixedEvent, Principal principal);
    UpdateEventStatus updateFixedEvent(FixedEvent fixedEvent, Principal principal);
    DeleteEventStatus deleteFixedEvent (Long id, Principal principal);
    List<EventDTO> createVariableEvent(VariableEvent variableEvent, Principal principal);
    UpdateEventStatus updateVariableEvent(VariableEvent variableEvent, Principal principal);
    DeleteEventStatus deleteVariableEvent (Long id, Principal principal);
    public List<EventDTO> getEvents(LocalDate firstDate, LocalDate secondDate, Principal principal);
    Optional<FixedEvent> findFixedEventById(Long id);

    Optional<VariableEvent> findVariableEventById(Long id);

    List<EventDTO> rescheduleVariableEvents(Principal principal);
}
