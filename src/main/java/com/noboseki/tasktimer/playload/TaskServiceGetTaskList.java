package com.noboseki.tasktimer.playload;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskServiceGetTaskList {

    private String taskName;
    private String time;
    private int sessionsNumber;
    private boolean complete;
}
