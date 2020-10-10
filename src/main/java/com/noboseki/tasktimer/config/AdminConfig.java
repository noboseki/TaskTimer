package com.noboseki.tasktimer.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AdminConfig {

    @Value("${noboseki.security.admin.name}")
    private String adminName;

    @Value("${noboseki.security.admin.password}")
    private String adminPassword;

    @Value("${noboseki.security.user.name}")
    private String userName;

    @Value("noboseki.security.user.password")
    private String userPassword;
}
