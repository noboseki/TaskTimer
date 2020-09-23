package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID privateID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean complete = false;
}
