package com.group13.academicplannerbackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    @JsonProperty
    private boolean isReschedulable;
    private String details;
    @Enumerated(EnumType.STRING)
    private EventPriority eventPriority;
    @JsonProperty
    private boolean isRepeat;
    @Enumerated(EnumType.STRING)
    private EventCategory eventCategory;
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public String getEventDTOName()
    {
        return name;
    }

}
