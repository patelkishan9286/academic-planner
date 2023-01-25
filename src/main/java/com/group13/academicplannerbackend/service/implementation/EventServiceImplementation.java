package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.model.*;
import com.group13.academicplannerbackend.repository.FixedEventRepository;
import com.group13.academicplannerbackend.repository.UserRepository;
import com.group13.academicplannerbackend.repository.VariableEventRepository;
import com.group13.academicplannerbackend.service.EventService;
import org.apache.commons.lang3.SerializationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImplementation implements EventService {
    private FixedEventRepository fixedEventRepository;
    private VariableEventRepository variableEventRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private static final int year = 2030;
    private static final int month = 12;
    private static final int day = 31;
    private static final int divisor = 7;

    @Autowired
    public EventServiceImplementation(FixedEventRepository fixedEventRepository,
            VariableEventRepository variableEventRepository,
            UserRepository userRepository,
            ModelMapper modelMapper) {
        this.fixedEventRepository = fixedEventRepository;
        this.variableEventRepository = variableEventRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * @param fixedEvent
     */
    @Override
    public List<EventDTO> createFixedEvent(FixedEvent fixedEvent, Principal principal) {
        if (fixedEvent.isRepeat()) {
            RepeatEvent repeatEvent = fixedEvent.getRepeatEvent();
            repeatEvent.setEvent(fixedEvent);
            fixedEvent.setRepeatEvent(repeatEvent);
        }
        User user = userRepository.findByEmail(principal.getName());
        fixedEvent.setUser(user);
        fixedEventRepository.save(fixedEvent);

        return rescheduleVariableEvents(principal);
    }

    @Override
    public UpdateEventStatus updateFixedEvent(FixedEvent fixedEvent, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        FixedEvent checkInsideDB = fixedEventRepository.findById(fixedEvent.getId()).orElse(null);

        if (checkInsideDB != null) {
            // Check if the user is authorized to update the event
            if (!user.getId().equals(checkInsideDB.getUser().getId())) {
                return UpdateEventStatus.NOT_AUTHORIZED;
            }

            if (checkInsideDB.isReschedulable()) {
                if (fixedEvent.isRepeat()) {
                    RepeatEvent repeatEvent = fixedEvent.getRepeatEvent();
                    repeatEvent.setEvent(fixedEvent);
                    fixedEvent.setRepeatEvent(repeatEvent);
                    fixedEvent.setUser(user);
                }
                fixedEventRepository.save(fixedEvent);
                return UpdateEventStatus.SUCCESS;
            } else {
                return UpdateEventStatus.NOT_RESCHEDULABLE;
            }
        } else {
            return UpdateEventStatus.NOT_FOUND;
        }
    }

    @Override
    public DeleteEventStatus deleteFixedEvent(Long id, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        FixedEvent checkInsideDB = fixedEventRepository.findById(id).orElse(null);
        if (checkInsideDB != null) {
            // Check if the user is authorized to update the event
            if (!user.getId().equals(checkInsideDB.getUser().getId())) {
                return DeleteEventStatus.NOT_AUTHORIZED;
            }

            fixedEventRepository.deleteById(id);
            return DeleteEventStatus.SUCCESS;
        } else {
            return DeleteEventStatus.NOT_FOUND;
        }
    }

    /**
     * @param variableEvent
     * @return
     */
    @Override
    public List<EventDTO> createVariableEvent(VariableEvent variableEvent, Principal principal) {

        if (variableEvent == null) {
            return null;
        }

        User user = userRepository.findByEmail(principal.getName());
        variableEvent.setUser(user);

        Schedule schedule = new Schedule();
        variableEvent.setSchedule(schedule);
        schedule.setVariableEvent(variableEvent);
        variableEventRepository.save(variableEvent);
        List<EventDTO> unscheduledVariableEvents = rescheduleVariableEvents(principal);
        return unscheduledVariableEvents;
    }

    @Override
    public List<EventDTO> rescheduleVariableEvents(Principal principal) {
        List<EventDTO> unscheduledVariableEvents = new ArrayList<>();

        ZoneId atlanticTimeZone = ZoneId.of("America/Halifax");
        ZonedDateTime atlanticZonedDateTime = ZonedDateTime.now(atlanticTimeZone);
        LocalDateTime currentDateTime = atlanticZonedDateTime.toLocalDateTime();
        LocalDate currentDate = currentDateTime.toLocalDate();
        LocalDate endDate = LocalDate.of(year, month, day);
        // List<EventDTO> fixedEventDTOs = getFixedEvents(currentDate, LocalDate.MAX,
        // principal);
        List<EventDTO> fixedEventDTOs = getFixedEvents(currentDate, endDate, principal);
        List<VariableEvent> variableEvents = variableEventRepository.findAllByUserEmail(principal.getName());
        fixedEventDTOs.sort(Comparator.comparing(EventDTO::getStartDate).thenComparing(EventDTO::getStartTime));
        variableEvents.sort(Comparator.comparing(VariableEvent::getDeadline));

        for (VariableEvent variableEvent : variableEvents) {
            LocalDateTime scheduledDateTime = findStartTimeForVariableEvent(variableEvent, fixedEventDTOs,
                    currentDateTime, variableEvent.getDeadline());
            variableEvent.getSchedule().setScheduledDateTime(scheduledDateTime);
            EventDTO varEventDTO = variableEventToDTO(variableEvent);
            if (scheduledDateTime != null) {
                variableEventRepository.save(variableEvent);
                fixedEventDTOs.add(varEventDTO);
            } else {
                unscheduledVariableEvents.add(varEventDTO);
                variableEventRepository.deleteById(variableEvent.getId());
            }
        }

        return unscheduledVariableEvents;
    }

    public EventDTO variableEventToDTO(VariableEvent variableEvent) {
        EventDTO varEventDTO = modelMapper.map(variableEvent, EventDTO.class);
        if (variableEvent.getSchedule().getScheduledDateTime() != null) {
            varEventDTO.setStartDate(variableEvent.getSchedule().getScheduledDateTime().toLocalDate());
            varEventDTO.setStartTime(variableEvent.getSchedule().getScheduledDateTime().toLocalTime());
            varEventDTO.setEndDate(
                    variableEvent.getSchedule().getScheduledDateTime().plus(variableEvent.getDuration()).toLocalDate());
            varEventDTO.setEndTime(
                    variableEvent.getSchedule().getScheduledDateTime().plus(variableEvent.getDuration()).toLocalTime());
        }
        varEventDTO.setReschedulable(true);
        varEventDTO.setEventType(EventType.VARIABLE);
        return varEventDTO;
    }

    @Override
    public UpdateEventStatus updateVariableEvent(VariableEvent variableEvent, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        VariableEvent checkInsideDB = variableEventRepository.findById(variableEvent.getId()).orElse(null);
        if (checkInsideDB != null) {
            if (!user.getId().equals(checkInsideDB.getUser().getId())) {
                return UpdateEventStatus.NOT_AUTHORIZED;
            }
            variableEvent.setUser(user);
            variableEventRepository.save(variableEvent);
            return UpdateEventStatus.SUCCESS;
        } else {
            return UpdateEventStatus.NOT_FOUND;
        }

    }

    @Override
    public DeleteEventStatus deleteVariableEvent(Long id, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        VariableEvent checkInsideDB = variableEventRepository.findById(id).orElse(null);
        if (checkInsideDB != null) {
            if (!user.getId().equals(checkInsideDB.getUser().getId())) {
                return DeleteEventStatus.NOT_AUTHORIZED;
            }
            variableEventRepository.deleteById(id);
            return DeleteEventStatus.SUCCESS;
        } else {
            return DeleteEventStatus.NOT_FOUND;
        }

    }

    /**
     * @return
     */
    @Override
    public List<EventDTO> getEvents(LocalDate firstDate, LocalDate secondDate, Principal principal) {
        List<EventDTO> eventDTOs = getFixedEvents(firstDate, secondDate, principal);
        LocalDateTime firstDateTime = LocalDateTime.of(firstDate, LocalTime.MIN);
        LocalDateTime secondDateTime = LocalDateTime.of(secondDate, LocalTime.MAX);

        List<VariableEvent> variableEvents = variableEventRepository
                .findAllByStartDateOrEndDateBetweenDates(firstDateTime, secondDateTime, principal.getName());
        List<EventDTO> variableEventDTOs = new ArrayList<>();
        for (VariableEvent varEvent : variableEvents) {
            EventDTO varEventDTO = modelMapper.map(varEvent, EventDTO.class);
            varEventDTO.setStartDate(varEvent.getSchedule().getScheduledDateTime().toLocalDate());
            varEventDTO.setStartTime(varEvent.getSchedule().getScheduledDateTime().toLocalTime());
            varEventDTO.setEndDate(
                    varEvent.getSchedule().getScheduledDateTime().plus(varEvent.getDuration()).toLocalDate());
            varEventDTO.setEndTime(
                    varEvent.getSchedule().getScheduledDateTime().plus(varEvent.getDuration()).toLocalTime());
            varEventDTO.setReschedulable(true);
            varEventDTO.setEventType(EventType.VARIABLE);
            variableEventDTOs.add(varEventDTO);
        }

        eventDTOs.addAll(variableEventDTOs);
        return eventDTOs;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<FixedEvent> findFixedEventById(Long id) {
        return fixedEventRepository.findById(id);
    }

    @Override
    public Optional<VariableEvent> findVariableEventById(Long id) {
        return variableEventRepository.findById(id);
    }

    /**
     * @return
     */
    public List<EventDTO> getFixedEvents(LocalDate firstDate, LocalDate secondDate, Principal principal) {
        List<FixedEvent> events = fixedEventRepository.findAllNonRepeatingByStartDateOrEndDateBetweenDates(firstDate,
                secondDate, principal.getName());
        List<FixedEvent> repeatingEvents = fixedEventRepository.findAllRepeatingByEndDateGreaterThanDate(firstDate,
                principal.getName());

        for (FixedEvent e : repeatingEvents) {
            RepititionType repititionType = e.getRepeatEvent().getRepititionType();

            int daysDiffBetweenStartAndEnd = Period.between(e.getStartDate(), e.getEndDate()).getDays();
            LocalDate date1;
            if (e.getStartDate().isEqual(firstDate) || e.getStartDate().isAfter(firstDate)) {
                date1 = e.getStartDate();
            } else {
                date1 = firstDate.minusDays(daysDiffBetweenStartAndEnd);
            }

            LocalDate date2;
            if (e.getRepeatEvent().getEndDate().isBefore(secondDate)) {
                date2 = e.getRepeatEvent().getEndDate();
            } else {
                date2 = secondDate;
            }

            long daysDiffBetweenDate1AndDate2 = ChronoUnit.DAYS.between(date1, date2);

            for (long i = 0; i <= daysDiffBetweenDate1AndDate2; i++) {
                if (isDaily(repititionType) || isWeeklyOnDay(repititionType, e, date1, i)) {
                    FixedEvent clonedFixedEvent = SerializationUtils.clone(e);
                    clonedFixedEvent.setStartDate(date1.plusDays(i));
                    clonedFixedEvent.setEndDate(date1.plusDays(i).plusDays(daysDiffBetweenStartAndEnd));
                    events.add(clonedFixedEvent);
                }
            }
        }

        List<EventDTO> eventDTOs = events.stream()
                .map(event -> modelMapper.map(event, EventDTO.class))
                .peek(eventDTO -> eventDTO.setEventType(EventType.FIXED))
                .collect(Collectors.toList());
        return eventDTOs;
    }

    private boolean isDaily(RepititionType repititionType) {
        return repititionType == RepititionType.DAILY;
    }

    private boolean isWeeklyOnDay(RepititionType repititionType, FixedEvent e, LocalDate date, long i) {

        boolean isWeekly = repititionType == RepititionType.WEEKLY;
        RepeatEvent repeatEvent = e.getRepeatEvent();
        boolean[] weeklyRepeatDays = repeatEvent.getWeeklyRepeatDays();
        int dayOfWeek = date.plusDays(i).getDayOfWeek().getValue() % divisor;
        boolean isOnWeeklyRepeatDay = isWeekly && weeklyRepeatDays[dayOfWeek];
        return isOnWeeklyRepeatDay;
    }

    private boolean isWithinFixedEvent(LocalDateTime startTime, LocalDateTime potentialEndTime,
            LocalDateTime fixedEventStart, LocalDateTime fixedEventEnd) {

        boolean isStartTimeEqualToStart = startTime.isEqual(fixedEventStart);
        boolean isStartTimeAfterStart = startTime.isAfter(fixedEventStart);
        boolean isStartTimeBeforeEnd = startTime.isBefore(fixedEventEnd);

        boolean isPotentialEndTimeAfterStart = potentialEndTime.isAfter(fixedEventStart);
        boolean isPotentialEndTimeBeforeEnd = potentialEndTime.isBefore(fixedEventEnd);

        boolean isStartTimeWithinFixedEvent = (isStartTimeEqualToStart || isStartTimeAfterStart)
                && isStartTimeBeforeEnd;
        boolean isPotentialEndTimeWithinFE = isPotentialEndTimeAfterStart && isPotentialEndTimeBeforeEnd;
        boolean isEventWithinFixedEvent = startTime.isBefore(fixedEventStart)
                && potentialEndTime.isAfter(fixedEventEnd);

        return isStartTimeWithinFixedEvent || isPotentialEndTimeWithinFE || isEventWithinFixedEvent;
    }

    public LocalDateTime findStartTimeForVariableEvent(VariableEvent variableEvent, List<EventDTO> fixedEventDTOs,
            LocalDateTime startDateTime, LocalDateTime endDateTime) {
        boolean slotFound = false;
        LocalDateTime potentialEndTime = LocalDateTime.MAX;
        while (!slotFound && startDateTime.isBefore(endDateTime)) {
            slotFound = true;

            for (EventDTO fixedEvent : fixedEventDTOs) {
                LocalDateTime fixedEventStart = LocalDateTime.of(fixedEvent.getStartDate(), fixedEvent.getStartTime());
                LocalDateTime fixedEventEnd = LocalDateTime.of(fixedEvent.getEndDate(), fixedEvent.getEndTime());

                potentialEndTime = startDateTime.plus(variableEvent.getDuration());

                if (isWithinFixedEvent(startDateTime, potentialEndTime, fixedEventStart, fixedEventEnd)) {
                    startDateTime = fixedEventEnd;
                    slotFound = false;
                    break;
                }
            }
        }

        LocalDateTime deadline = variableEvent.getDeadline();
        boolean endTimeIsBeforeDeadline = potentialEndTime.isBefore(deadline) || potentialEndTime.isEqual(deadline);

        if (slotFound && endTimeIsBeforeDeadline) {
            return startDateTime;
        } else {
            return null;
        }
    }
}
