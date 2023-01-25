package com.group13.academicplannerbackend.service;

import com.group13.academicplannerbackend.model.JwtResponse;
import com.group13.academicplannerbackend.model.User;

public interface UserService {
    void register(User user);
    JwtResponse loginProcess(User user);

    void updateProfileStatus(String name);
}
