package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClassCreator {

    private final PasswordEncoder passwordEncoder;

    public User user(){
        return User.builder()
                .username("user")
                .email("test1@test.com")
                .password(passwordEncoder.encode("password")).build();
    }

    public Task task() {
        return Task.builder()
                .name("Test name").build();
    }
}
