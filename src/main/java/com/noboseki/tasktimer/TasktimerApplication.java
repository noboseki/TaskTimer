package com.noboseki.tasktimer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class TasktimerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasktimerApplication.class, args);
    }

}
