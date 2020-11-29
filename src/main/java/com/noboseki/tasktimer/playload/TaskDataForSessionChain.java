package com.noboseki.tasktimer.playload;

import lombok.*;

import java.util.List;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class TaskDataForSessionChain {

    private List<Float> data;
    private String taskName;
}
