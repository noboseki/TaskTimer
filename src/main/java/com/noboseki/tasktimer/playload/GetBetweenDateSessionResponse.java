package com.noboseki.tasktimer.playload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class GetBetweenDateSessionResponse {

    private LocalDate date;
    private String time;
    private long timeByNumber;
    private int sessions;
}
