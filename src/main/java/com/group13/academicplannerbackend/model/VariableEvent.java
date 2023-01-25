package com.group13.academicplannerbackend.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class VariableEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String details;

    @Enumerated(EnumType.STRING)
    private EventPriority eventPriority;

    @Enumerated(EnumType.STRING)
    private EventCategory eventCategory;

    private Duration duration;

    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToOne(mappedBy = "variableEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Schedule schedule;

    public String getVariableEventName()
    {
        return name;
    }
}
