package com.group13.academicplannerbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class RepeatEvent implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "event_id")
    @JsonBackReference
    private FixedEvent event;

    @Enumerated(EnumType.STRING)
    private RepititionType repititionType;

    private boolean[] weeklyRepeatDays;
    private LocalDate endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FixedEvent getEvent() {
        return event;
    }

    public void setEvent(FixedEvent event) {
        this.event = event;
    }

    public RepititionType getRepititionType() {
        return repititionType;
    }

    public void setRepititionType(RepititionType repititionType) {
        this.repititionType = repititionType;
    }

    public boolean[] getWeeklyRepeatDays() {
        return weeklyRepeatDays;
    }

    public void setWeeklyRepeatDays(boolean[] weeklyRepeatDays) {
        this.weeklyRepeatDays = weeklyRepeatDays;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
