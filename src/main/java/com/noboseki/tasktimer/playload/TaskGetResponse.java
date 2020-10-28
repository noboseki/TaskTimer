package com.noboseki.tasktimer.playload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskGetResponse {

    private String name;
    private boolean complete;
}
