package com.noboseki.tasktimer.playload;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class SessionServiceTableByDateResponse {

    private LocalDate date;
    private String time;
    private int sessions;
}
