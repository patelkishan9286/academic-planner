package com.group13.academicplannerbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private ProfileStatus profileStatus;

    public String getToken() {
        return token;
    }
}
