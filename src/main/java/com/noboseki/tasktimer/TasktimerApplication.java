package com.noboseki.tasktimer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.noboseki.tasktimer.repository")
public class TasktimerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasktimerApplication.class, args);
    }

}
