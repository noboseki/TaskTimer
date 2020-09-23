package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "work_time")
public class WorkTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID privateID;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Time time;
}
