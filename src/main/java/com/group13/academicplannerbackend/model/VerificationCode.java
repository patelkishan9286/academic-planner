package com.group13.academicplannerbackend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    private String email;
    private LocalDateTime expiryTime;

    public VerificationCode(String code, String email, LocalDateTime expiryTime) {
        this.code = code;
        this.email = email;
        this.expiryTime = expiryTime;
    }

    public VerificationCode() {

    }
}
