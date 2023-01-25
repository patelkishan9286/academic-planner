package com.group13.academicplannerbackend.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class UserMeta implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private boolean verified;

    @Enumerated(EnumType.STRING)
    private ProfileStatus profileStatus;

    public boolean isVerified() {
        return verified;
    }
}
