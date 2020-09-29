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
    private String date;

    @Column(nullable = false)
    private String time;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkTimeDto {
        @NotNull
        private UUID privateID;
        @NotNull
        private String date;
        @NotNull
        private String time;
    }
}
