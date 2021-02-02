package com.noboseki.tasktimer.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class PropertiesConstants {

    @Value("${spring.mail.username}")
    private String email;
}