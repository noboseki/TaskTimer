package com.noboseki.tasktimer.playload;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class GetByTaskSessionResponse {

    private Date date;
    private Time time;
}
