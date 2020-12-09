package com.noboseki.tasktimer.playload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskServiceGetTaskList {

    private String taskName;
    private String time;
    private int sessionsNumber;
    private boolean complete;
}
