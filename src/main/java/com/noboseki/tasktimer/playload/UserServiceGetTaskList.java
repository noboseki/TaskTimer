package com.noboseki.tasktimer.playload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceGetTaskList {

    private String taskName;
    private String time;
    private boolean isComplete;
}
