package com.group13.academicplannerbackend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "variable_event_id")
    @JsonBackReference
    private VariableEvent variableEvent;

    private LocalDateTime scheduledDateTime;

    public  long getScheduleID()
    {
        return id;
    }
}
