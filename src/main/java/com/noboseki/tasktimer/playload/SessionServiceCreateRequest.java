package com.noboseki.tasktimer.playload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionServiceCreateRequest {

    @NotNull
    private String date;
    @NotNull
    private String time;
    @NotNull
    private String taskName;

}
