package com.noboseki.tasktimer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Time;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "work_time")
@NoArgsConstructor
@AllArgsConstructor
public class WorkTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID privateID;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Time time;

    @Data
    @Builder
    public static class WorkTimeDto {
        @NotNull
        private UUID privateID;
        @NotNull
        private Date date;
        @NotNull
        private Time time;
    }
}
