package com.noboseki.tasktimer.playload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class SessionServiceTableByDateResponse {

    private LocalDate date;
    private String time;
    private int sessions;
}
